package ru.simplex_software.smeta.model;

import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TaskFilter extends LongIdPersistentEntity{

    @OneToOne
    private City city;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "filter_city",
               joinColumns = {@JoinColumn(name = "taskfilter_id")},
               inverseJoinColumns = {@JoinColumn(name = "city_id")})
    private Set<City> cities = new HashSet<>();

    private Date startDate;

    private Date endDate;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }
}
