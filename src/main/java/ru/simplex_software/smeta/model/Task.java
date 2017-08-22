package ru.simplex_software.smeta.model;

import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Task extends LongIdPersistentEntity {

    @NotNull
    private String name;

    private String shopName;

    /** Сумма по заданию. **/
    private Double amount;

    private boolean isFilled = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private List<Work> works;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }
}
