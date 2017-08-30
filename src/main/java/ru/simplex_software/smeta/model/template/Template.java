package ru.simplex_software.smeta.model.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Work;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Template extends LongIdPersistentEntity {

    private static Logger LOG = LoggerFactory.getLogger(Template.class);

    /** Имя. **/
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "template")
    private List<Work> workList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "template")
    private List<Material> materialList;

    public Template() {}

    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
