package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Work extends LongIdPersistentEntity implements Cloneable {

    private static Logger LOG = LoggerFactory.getLogger(Task.class);

    private String name;

    /** Единицы измерения. **/
    private String units;

    /** Кол-во. **/
    private Double quantity;

    /** Цена за единицу. **/
    private Double unitPrice;

    /** Сумма. **/
    private Double amount;

    private boolean editingStatus = true;

    @ManyToOne
    private Task task;

    public Work(String name, String units, Double quantity, Double unitPrice, Double amount, Task task) {
        this.name = name;
        this.units = units;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.task = task;
    }

    public Work() {}

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

    public ru.simplex_software.smeta.model.Task getTask() {
        return task;
    }

    public void setTask(ru.simplex_software.smeta.model.Task task) {
        this.task = task;
    }

    public boolean isEditingStatus() {
        return editingStatus;
    }

    public void setEditingStatus(boolean editingStatus) {
        this.editingStatus = editingStatus;
    }

    public static Work clone(Work work) {
        try {
            return (Work) work.clone();
        } catch (CloneNotSupportedException e) {
            // not possible
        }
        return null;
    }

}
