package ru.simplex_software.smeta.model;

import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Manager extends LongIdPersistentEntity {

    @Column(unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
