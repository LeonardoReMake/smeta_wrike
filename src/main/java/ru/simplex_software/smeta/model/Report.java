package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Report {

    private static Logger LOG = LoggerFactory.getLogger(Report.class);

    /* Список городов. */
    @OneToMany
    private List<City> cityList = new ArrayList<>();

    /* Номер договора. */
    private int number;

    public Report() {}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

}
