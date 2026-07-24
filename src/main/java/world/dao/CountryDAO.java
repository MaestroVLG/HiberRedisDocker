package world.dao;

import org.hibernate.Session;
import world.domain.Country;

public class CountryDAO {

    private final Session session;

    public CountryDAO(Session session) {
        this.session = session;
    }

    public Country getById(String code) {
        return session.get(Country.class, code);
    }
}