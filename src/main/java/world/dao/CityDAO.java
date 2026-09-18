package world.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import world.domain.City;

import java.util.List;

public class CityDAO {

    private final SessionFactory sessionFactory;

    public CityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int getTotalCount() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Query<Long> query = session.createQuery("select count(c) from City c", Long.class);
            return Math.toIntExact(query.uniqueResult());
        }
    }

    public List<City> getItems(int firstResult, int maxResults) {
        try (Session session = sessionFactory.getCurrentSession()) {
            Query<City> query = session.createQuery(
                    "select c from City c order by c.id", City.class
            );
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            return query.list();
        }
    }
}