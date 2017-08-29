package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.model.Element;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;
import ru.simplex_software.zkutils.DetachableModel;

import java.util.List;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class WorkAndMaterialViewModel {

    private static Logger LOG = LoggerFactory.getLogger(WorkAndMaterialViewModel.class);

    @WireVariable
    private WorkDAO workDAO;

    @WireVariable
    private MaterialDAO materialDAO;

    @DetachableModel
    private Task task;

    private ListModelList<Work> workListModel;

    private ListModelList<Material> materialListModel;

    private Work work;

    private Material material;

    private boolean addWork;

    private boolean addMaterial;

    public ListModelList<Material> getMaterialListModel() {
        return materialListModel;
    }

    public void setMaterialListModel(ListModelList<Material> materialListModel) {
        this.materialListModel = materialListModel;
    }

    public ListModelList<Work> getWorkListModel() {
        return workListModel;
    }

    public void setWorkListModel(ListModelList<Work> workListModel) {
        this.workListModel = workListModel;
    }

    public boolean isAddWork() {
        return addWork;
    }

    public void setAddWork(boolean addWork) {
        this.addWork = addWork;
    }

    public boolean isAddMaterial() {
        return addMaterial;
    }

    public void setAddMaterial(boolean addMaterial) {
        this.addMaterial = addMaterial;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Element getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @AfterCompose
    public void init(@ExecutionArgParam("task") Task task) {
        List<Work> workList = workDAO.findByWorks(task);
        workListModel = new ListModelList<>(workList);

        List<Material> materialList = materialDAO.findByMaterials(task);
        materialListModel = new ListModelList<>(materialList);

        this.task = task;
    }

    @Command
    @NotifyChange("addWork")
    public void onChangeVisibilityAddWork() {
        if (addWork) {
            addWork = false;
        } else {
            work = new Work(this.task);
            addWork = true;
        }
    }

    @Command
    @NotifyChange("workListModel")
    public void updateNewWork(@BindingParam("work") Work work) {
        workListModel.set(workListModel.indexOf(work), work);
        workDAO.saveOrUpdate(work);
    }

    @Command
    @NotifyChange("workListModel")
    public void deleteWork(@BindingParam("work") Work work) {
        workListModel.remove(work);
        workDAO.delete(workDAO.get(work.getId()));
    }

    @Command
    @NotifyChange({"workListModel", "addWork", "work"})
    public void addNewWork() {
        workListModel.add(work);
        workDAO.saveOrUpdate(work);
        work = null;
        addWork = false;
    }

    /* materials */

    @Command
    @NotifyChange("addMaterial")
    public void onChangeVisibilityAddMaterial() {
        if (addMaterial) {
            addMaterial = false;
        } else {
            this.material = new Material(this.task);
            addMaterial = true;
        }
    }

    @Command
    @NotifyChange({"materialListModel", "material", "addMaterial"})
    public void addNewMaterial() {
        materialListModel.add(material);
        materialDAO.saveOrUpdate(material);
        material = null;
        addMaterial = false;
    }

    @Command
    @NotifyChange("materialListModel")
    public void updateNewMaterial(@BindingParam("material") Material material) {
        materialListModel.set(materialListModel.indexOf(material), material);
        materialDAO.saveOrUpdate(material);
    }

    @Command
    @NotifyChange("materialListModel")
    public void deleteMaterial(@BindingParam("material") Material material) {
        materialListModel.remove(material);
        materialDAO.delete(materialDAO.get(material.getId()));
    }

}
