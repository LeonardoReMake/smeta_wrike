package ru.simplex_software.smeta.model;

import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Material extends LongIdPersistentEntity {

    private String name;

    /** Единицы измерения. **/
    private String units;

    /** Кол-во. **/
    private Double quantity;

    /** Цена за единицу. **/
    private Double unitPrice;

    /** Сумма. **/
    private Double amount;

    @ManyToOne
    private Work work;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }
}
