package world.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.*;
import world.redis.CityCountry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RedisIntegrationTest {

    private RedisClient redisClient;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        // Подключаемся к локальному Redis (запущен вручную)
        redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        mapper = new ObjectMapper();
    }

    @Test
    void testRedisData() throws Exception {
        // given
        CityCountry city = new CityCountry();
        city.setId(1);
        city.setName("TestCity");


        try (StatefulRedisConnection<String, String> conn = redisClient.connect()) {
            String json = mapper.writeValueAsString(city);
            conn.sync().set("1", json);
        }


        try (StatefulRedisConnection<String, String> conn = redisClient.connect()) {
            String json = conn.sync().get("1");
            assertNotNull(json);

            CityCountry loaded = mapper.readValue(json, CityCountry.class);
            assertEquals("TestCity", loaded.getName());

            System.out.println("Redis test passed: " + json);
        }
    }

    @AfterEach
    void tearDown() {
        if (redisClient != null) redisClient.shutdown();
    }
}