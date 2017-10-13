package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ReportElement {

    private static Logger LOG = LoggerFactory.getLogger(ReportElement.class);

    private String shopName;

    private City city;

    private List<String> taskOrders = new ArrayList<>();

    private List<Work> works = new ArrayList<>();

    private List<Material> materials = new ArrayList<>();

    public ReportElement() { }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<String> getTaskOrders() {
        return taskOrders;
    }

    public void setTaskOrders(List<String> taskOrders) {
        this.taskOrders = taskOrders;
    }

}
