package world.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "country", schema = "world")
public class Country {
    @Id
    @Column(name = "Code")
    private String code;

    @Column(name = "Name")
    private String name;

    @Column(name = "Continent")
    private String continent;

    @Column(name = "Region")
    private String region;

    @Column(name = "SurfaceArea")
    private Double surfaceArea;

    @Column(name = "IndepYear")
    private Integer indepYear;

    @Column(name = "Population")
    private Integer population;

    @Column(name = "LifeExpectancy")
    private Double lifeExpectancy;

    @Column(name = "GNP")
    private Double gnp;

    @Column(name = "GNPOld")
    private Double gnpOld;

    @Column(name = "LocalName")
    private String localName;

    @Column(name = "GovernmentForm")
    private String governmentForm;

    @Column(name = "HeadOfState")
    private String headOfState;

    @Column(name = "Capital")
    private Integer capital;

    @Column(name = "Code2")
    private String code2;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private Set<City> cities = new HashSet<>();

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private Set<CountryLanguage> languages = new HashSet<>();

    // getters and setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Double getSurfaceArea() { return surfaceArea; }
    public void setSurfaceArea(Double surfaceArea) { this.surfaceArea = surfaceArea; }

    public Integer getIndepYear() { return indepYear; }
    public void setIndepYear(Integer indepYear) { this.indepYear = indepYear; }

    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }

    public Double getLifeExpectancy() { return lifeExpectancy; }
    public void setLifeExpectancy(Double lifeExpectancy) { this.lifeExpectancy = lifeExpectancy; }

    public Double getGnp() { return gnp; }
    public void setGnp(Double gnp) { this.gnp = gnp; }

    public Double getGnpOld() { return gnpOld; }
    public void setGnpOld(Double gnpOld) { this.gnpOld = gnpOld; }

    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }

    public String getGovernmentForm() { return governmentForm; }
    public void setGovernmentForm(String governmentForm) { this.governmentForm = governmentForm; }

    public String getHeadOfState() { return headOfState; }
    public void setHeadOfState(String headOfState) { this.headOfState = headOfState; }

    public Integer getCapital() { return capital; }
    public void setCapital(Integer capital) { this.capital = capital; }

    public String getCode2() { return code2; }
    public void setCode2(String code2) { this.code2 = code2; }

    public Set<City> getCities() { return cities; }
    public void setCities(Set<City> cities) { this.cities = cities; }

    public Set<CountryLanguage> getLanguages() { return languages; }
    public void setLanguages(Set<CountryLanguage> languages) { this.languages = languages; }
}