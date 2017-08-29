package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Material extends Element {

    private static Logger LOG = LoggerFactory.getLogger(Material.class);

    @ManyToOne
    private Task task;

    public Material(String name, String units, Double quantity, Double unitPrice, Double amount, Task task) {
        super(name, units, quantity, unitPrice, amount);
        this.task = task;
    }

    public Material(Task task) {
        this.task = task;
    }

    public Material() {}

}
