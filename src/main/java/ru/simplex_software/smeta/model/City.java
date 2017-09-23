package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class City extends LongIdPersistentEntity {
    private static Logger LOG = LoggerFactory.getLogger(City.class);

    /* Название города. */
    private String name;

    /** Документ, к которому относится город. **/
    @ManyToOne
    private Report report;

    public City() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
