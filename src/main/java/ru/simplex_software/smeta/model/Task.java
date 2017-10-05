package ru.simplex_software.smeta.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends LongIdPersistentEntity {

    private static Logger LOG = LoggerFactory.getLogger(Task.class);

    /** id, которое дает wrike каждой задаче. **/
    @NotNull
    private String wrikeId;

    /** Полное название задачи, указанное в wrike. **/
    @NotNull
    private String name;

    /** Название магазина, с которым связана задача. **/
    @Column(length = 1024)
    private String shopName;

    /** Сумма по заданию. **/
    private Double amount;

    /** Указывает заполнена ли задача работами. **/
    private boolean filled = false;

    /** Работы, которые надо выполнить по этой задаче. **/
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private List<Work> works = new ArrayList<>();

    /** Материалы, которые надо выполнить по этой задаче. **/
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private List<Material> materials = new ArrayList<>();

    /** Дата создания задачи. **/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdDate;

    /** Название города. **/
    @OneToOne
    private City city;

    /** Номер заявки. Например: INC1343892. **/
    private String orderNumber;

    /** Ссылка на задачу в wrike. **/
    private String wrikeLink;

    /** Путь к задаче в wrike. **/
    private String path;

    /** Id папок в wrike. **/
    @Transient
    private List<String> parentIds;

    /** Дата завершания задачи. **/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime completedDate;

    @Transient
    private boolean departure;

    public Task(String wrikeId, String name, String shopName, Double amount, boolean filled) {
        this.wrikeId = wrikeId;
        this.name = name;
        this.shopName = shopName;
        this.amount = amount;
        this.filled = filled;
    }

    public Task() {}

    public boolean isFilled() {
        return !(works.isEmpty() &&
                 materials.isEmpty());
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

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public String getWrikeLink() {
        return wrikeLink;
    }

    @JsonSetter("permalink")
    public void setWrikeLink(String wrikeLink) {
        this.wrikeLink = wrikeLink;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isDeparture() {
        return departure;
    }

    public void setDeparture(boolean departure) {
        this.departure = departure;
    }

}
