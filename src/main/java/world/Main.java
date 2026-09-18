package world;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import world.domain.City;
import world.domain.Country;
import world.domain.CountryLanguage;
import world.dao.CityDAO;
import world.redis.CityCountry;
import world.redis.Language;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Main {

    public final SessionFactory sessionFactory;
    public RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityDAO cityDAO;

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData();
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);

        // Закрываем сессию, чтобы точно делать запрос к БД, а не вытянуть данные из кэша
        main.sessionFactory.getCurrentSession().close();

        // Выбираем случайные 10 id городов (из существующих в БД)
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        main.shutdown();
    }

    public Main() {
        sessionFactory = prepareRelationalDb();
        cityDAO = new CityDAO(sessionFactory);

        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    private List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            // Загружаем cities с предзагрузкой country и languages
            List<City> allCities = session.createQuery(
                    "select distinct c from City c " +
                            "join fetch c.country co " +
                            "left join fetch co.languages " +
                            "order by c.id", City.class
            ).list();

            session.getTransaction().commit();
            return allCities;
        }
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setName(city.getName());
            res.setPopulation(city.getPopulation());
            res.setDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());

            List<Language> languages = country.getLanguages().stream()
                    .map(cl -> {
                        Language lang = new Language();
                        lang.setLanguage(cl.getLanguage());
                        lang.setOfficial(cl.getOfficial());
                        lang.setPercentage(cl.getPercentage());
                        return lang;
                    }).collect(Collectors.toList());

            res.setLanguages(languages);

            return res;
        }).collect(Collectors.toList());
    }

    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            for (CityCountry cityCountry : data) {
                try {
                    String key = String.valueOf(cityCountry.getId());
                    String json = mapper.writeValueAsString(cityCountry);
                    connection.sync().set(key, json);
                    //System.out.println("Saved to Redis: " + key);
                } catch (JsonProcessingException e) {
                    System.err.println("Failed to serialize city " + cityCountry.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public RedisClient prepareRedisClient() {
        redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            connection.sync().ping();
            System.out.println("Connected to Redis");
        } catch (Exception e) {
            System.err.println("Redis is not available: " + e.getMessage());
            throw new RuntimeException("Redis connection failed", e);
        }
        return redisClient;
    }

    private SessionFactory prepareRelationalDb() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/world");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "none");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");

        return new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    // ✅ метод для теста Redis
    public void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> conn = redisClient.connect()) {
            for (Integer id : ids) {
                String json = conn.sync().get(String.valueOf(id));
                if (json != null) {
                    System.out.println("Redis: Found city with ID " + id);
                } else {
                    System.out.println("Redis: City with ID " + id + " not found");
                }
            }
        }
    }

    // ✅ метод для теста MySQL
    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.openSession()) {
            for (Integer id : ids) {
                City city = session.get(City.class, id);
                if (city != null) {
                    System.out.println("MySQL: Found city with ID " + id);
                } else {
                    System.out.println("MySQL: City with ID " + id + " not found");
                }
            }
        }
    }

    public void shutdown() {
        if (nonNull(sessionFactory)) sessionFactory.close();
        if (nonNull(redisClient)) redisClient.shutdown();
    }
}