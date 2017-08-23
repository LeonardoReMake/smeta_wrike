package ru.simplex_software.smeta.model;

import java.util.ArrayList;
import java.util.List;

/** Модель объект, который приходит от wrike. **/
public class WrikeObject {

    private String kind;

    private List<Task> data = new ArrayList<Task>();

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<Task> getData() {
        return data;
    }

    public void setData(List<Task> data) {
        this.data = data;
    }

}
