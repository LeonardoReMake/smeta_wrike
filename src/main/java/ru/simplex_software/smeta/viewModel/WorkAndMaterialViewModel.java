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
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;

import java.util.List;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class WorkAndMaterialViewModel {

    private static Logger LOG = LoggerFactory.getLogger(WorkAndMaterialViewModel.class);

    @WireVariable
    private WorkDAO workDAO;

    @WireVariable
    private MaterialDAO materialDAO;

    private ListModelList<Work> workListModel;

    private ListModelList<Material> materialListModel;

    private boolean addWork;

    private boolean addMaterial;

    private String name;

    /** Единицы измерения. **/
    private String units;

    /** Кол-во. **/
    private Double quantity;

    /** Цена за единицу. **/
    private Double unitPrice;

    /** Сумма. **/
    private Double amount;

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

    public long getTotalSize(Task task) {
        return workDAO.getAllWorksCount(task);
    }

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

    @AfterCompose
    public void init(@ExecutionArgParam("task") Task task) {
        List<Work> workList = workDAO.findByWorks(task);
        workListModel = new ListModelList<>(workList);

        List<Material> materialList = materialDAO.findByMaterials(task);
        materialListModel = new ListModelList<>(materialList);
    }

    @Command
    @NotifyChange("addWork")
    public void onChangeVisibilityAddWork() {
        if (addWork) {
            addWork = false;
        } else {
            addWork = true;
        }
    }

    @Command
    @NotifyChange("workListModel")
    public void addNewWork() {
        Work work = new Work();
        work = Work.clone(work);
        work.setName(name);
        work.setUnits(units);
        work.setQuantity(quantity);
        work.setUnits(units);
        work.setAmount(amount);

        workListModel.add(work);
        workDAO.create(work);
    }

    @Command
    @NotifyChange("workListModel")
    public void updateNewWork(@BindingParam("work") Work work) {
        if (work.getId() != null){
            work = Work.clone(work);
            for(int i = 0; i < workListModel.size(); i++){
                Work newWork = workListModel.get(i);
                if (newWork.getId().equals(work.getId())){
                    workListModel.set(i, work);
                    workDAO.saveOrUpdate(work);
                    return;
                }
            }
        }
    }

    @Command
    @NotifyChange("workListModel")
    public void deleteWork(@BindingParam("work") Work work) {
        if(work.getId() != null){

            for(int i = 0; i < workListModel.size(); i++){

                Work newWork = workListModel.get(i);
                if (newWork.getId().equals(work.getId())) {
                    workListModel.remove(i);
                    workDAO.delete(workDAO.get(work.getId()));
                    return;
                }
           }
        }

    }
    /* materials */

    @Command
    @NotifyChange("addMaterial")
    public void onChangeVisibilityAddMaterial() {
        if (addMaterial) {
            addMaterial = false;
        } else {
            addMaterial = true;
        }
    }

    @Command
    @NotifyChange("materialListModel")
    public void addNewMaterial() {
        Material material = new Material();
        material = Material.clone(material);
        material.setName(name);
        material.setUnits(units);
        material.setQuantity(quantity);
        material.setUnits(units);
        material.setAmount(amount);

        materialListModel.add(material);
        materialDAO.create(material);
    }

    @Command
    @NotifyChange("materialListModel")
    public void updateNewMaterial(@BindingParam("material") Material material) {
        if (material.getId() != null){
            material = Material.clone(material);
            for(int i = 0; i < materialListModel.size(); i++){
                Material newMaterial = materialListModel.get(i);
                if (newMaterial.getId().equals(material.getId())){
                    materialListModel.set(i, material);
                    materialDAO.saveOrUpdate(material);
                    return;
                }
            }
        }
    }

    @Command
    @NotifyChange("materialListModel")
    public void deleteMaterial(@BindingParam("work") Material material) {
        if(material.getId() != null){
            material = Material.clone(material);
            for(int i = 0; i < materialListModel.size(); i++){

                Material newMaterial = materialListModel.get(i);
                if (newMaterial.getId().equals(material.getId())) {
                    materialListModel.remove(i);
                    materialDAO.delete(materialDAO.get(material.getId()));
                    return;
                }
            }
        }

    }

}
