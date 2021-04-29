package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class City extends LongIdPersistentEntity {
    private static Logger LOG = LoggerFactory.getLogger(City.class);

    /* Название города. */
    @Column(length = 1024)
    private String name;

    /** Документ, к которому относится город. **/
    @ManyToOne
    private Report report;

    /** Задачи, которые нужно вывести на форму. **/
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "city")
    private List<Task> tasks = new ArrayList<>();

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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

}
