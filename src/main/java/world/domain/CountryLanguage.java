package world.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "countrylanguage", schema = "world")
public class CountryLanguage implements Serializable {
    @Id
    @Column(name = "CountryCode")
    private String countryCode;

    @Id
    @Column(name = "Language")
    private String language;

    @Column(name = "IsOfficial")
    private Boolean official;

    @Column(name = "Percentage")
    private Double percentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CountryCode", insertable = false, updatable = false)
    private Country country;


    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Boolean getOfficial() { return official; }
    public void setOfficial(Boolean official) { this.official = official; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryLanguage that = (CountryLanguage) o;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, language);
    }
}