package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Element extends LongIdPersistentEntity {

    private static Logger LOG = LoggerFactory.getLogger(Element.class);

    /** Название. **/
    private String name;

    /** Единицы измерения. **/
    private String units;

    /** Кол-во. **/
    private double quantity;

    /** Цена за единицу. **/
    private double unitPrice;

    /** Сумма. **/
    private double amount;

    @ManyToOne
    private Task task;

    public Element(String name, String units, double quantity, double unitPrice, double amount) {
        this.name = name;
        this.units = units;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    public Element() {}

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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
