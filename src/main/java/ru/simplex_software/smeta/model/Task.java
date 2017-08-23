package ru.simplex_software.smeta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.joda.time.LocalDate;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends LongIdPersistentEntity{

    /** id, которое дает wrike каждой задаче. **/
    @NotNull
    private String wrikeId;

    /** Полное название задачи, указанное в wrike. **/
    @NotNull
    private String name;

    /** Название магазина, с которым связана задача. **/
    private String shopName;

    /** Сумма по заданию. **/
    private Double amount;

    /** Указывает заполнена ли задача работами. **/
    private boolean isFilled = false;

    /** Работы, которые надо выполнить по этой задаче. **/
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private List<Work> works;

    /** Дата создания задачи. **/
    private LocalDate creationDate;

    /** Название города. **/
    private String city;

    /** Номер заявки. Например: INC1343892. **/
    private String orderNumber;

    @Override
    public String toString() {
        return "db_id: "+getId()+" name: "+name+" shopName: "+shopName+" city: "+city+" orderNumber: "+orderNumber;
    }

    public String getWrikeId() {
        return wrikeId;
    }

    @JsonSetter("id")
    public void setWrikeId(String id) {
        this.wrikeId = id;
    }

    public String getName() {
        return name;
    }

    @JsonSetter("title")
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
