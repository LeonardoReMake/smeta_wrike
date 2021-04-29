package ru.simplex_software.smeta.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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

    /** id, которое дает wrike каждой задаче. **/
    @NotNull
    @Column(unique = true)
    private String wrikeId;

    /** Полное название задачи, указанное в wrike. **/
    @NotNull
    @Column(length = 1024)
    private String name;

    /** Название магазина, с которым связана задача. **/
    @Column(length = 1024)
    private String shopName;

    /** Сумма по заданию. **/
    private Double amount;

    /** Указывает заполнена ли задача работами. **/
    private boolean filled = false;

    /** Указывает выбрана ли задача. **/
    private boolean checked = true;

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
    @ManyToOne
    private City city;

    /** Номер заявки. Например: INC1343892. **/
    @Column(length = 1024)
    private String orderNumber;

    /** Ссылка на задачу в wrike. **/
    @Column(length = 1024)
    private String wrikeLink;

    /** Путь к задаче в wrike. **/
    @Column(length = 1024)
    private String path;

    /** Id папок в wrike. **/
    @Transient
    private List<String> parentIds;

    /** Id супер папок в wrike. **/
    @Transient
    private List<String> superParentIds;

    /** Дата завершания задачи. **/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime completedDate;

    @Transient
    private boolean departure;

    /** Важность задачи: High, Normal, Low. **/
    private String importance;

    @OneToOne
    private Manager manager;

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

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

    public boolean getFilled() {
        return filled;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public List<String> getSuperParentIds() {
        return superParentIds;
    }

    public void setSuperParentIds(List<String> superParentIds) {
        this.superParentIds = superParentIds;
    }
}
