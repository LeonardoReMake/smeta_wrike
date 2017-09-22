package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ManyToOne;


public class City {

    private static Logger LOG = LoggerFactory.getLogger(City.class);

    /* Название города. */
    @ManyToOne
    private String name;

    public City() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
