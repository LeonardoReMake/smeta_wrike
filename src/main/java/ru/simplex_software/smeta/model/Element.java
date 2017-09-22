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
    private Double quantity;

    /** Цена за единицу. **/
    private Double unitPrice;

    /** Сумма. **/
    private Double amount;

    @ManyToOne
    private Task task;

    public Element(String name, String units, Double quantity, Double unitPrice, Double amount) {
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
        return amount == null ? 0 : amount;
    }

    public void setAmount(Double amount) {
        amount = unitPrice * quantity;
        this.amount = amount;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
