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
import ru.simplex_software.smeta.dao.TemplateDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.model.Element;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;
import ru.simplex_software.smeta.model.template.Template;
import ru.simplex_software.zkutils.DetachableModel;

import java.util.List;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class WorkAndMaterialViewModel {

    private static Logger LOG = LoggerFactory.getLogger(WorkAndMaterialViewModel.class);

    @WireVariable
    private WorkDAO workDAO;

    @WireVariable
    private MaterialDAO materialDAO;

    @WireVariable
    private TemplateDAO templateDAO;

    @DetachableModel
    private Task task;

    @DetachableModel
    private Template template;

    private ListModelList<Work> workListModel;

    private ListModelList<Material> materialListModel;

    private ListModelList<Template> templateWorkAndMaterialList;

    private Work work;

    private Material material;

    private boolean canWork;

    private boolean canMaterial;

    private boolean canToTemplate;

    private boolean canFromTemplate;

    public boolean isCanWork() {
        return canWork;
    }

    public void setCanWork(boolean canWork) {
        this.canWork = canWork;
    }

    public boolean isCanMaterial() {
        return canMaterial;
    }

    public void setCanMaterial(boolean canMaterial) {
        this.canMaterial = canMaterial;
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

    public ListModelList<Template> getTemplateWorkAndMaterialList() {
        return templateWorkAndMaterialList;
    }

    public void setTemplateWorkAndMaterialList(ListModelList<Template> templateWorkAndMaterialList) {
        this.templateWorkAndMaterialList = templateWorkAndMaterialList;
    }

    public void setWorkListModel(ListModelList<Work> workListModel) {
        this.workListModel = workListModel;
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

    public boolean isCanToTemplate() {
        return canToTemplate;
    }

    public void setCanToTemplate(boolean canToTemplate) {
        this.canToTemplate = canToTemplate;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public boolean isCanFromTemplate() {
        return canFromTemplate;
    }

    public void setCanFromTemplate(boolean canFromTemplate) {
        this.canFromTemplate = canFromTemplate;
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
    @NotifyChange("canWork")
    public void onChangeVisibilityAddWork() {
        if (!canWork) {
            work = new Work(this.task);
        }
        canWork = !canWork;
    }

    @Command
    @NotifyChange("workListModel")
    public void updateNewWork(@BindingParam("work") Work work) {
        countAmount(work);
        workDAO.saveOrUpdate(work);
    }

    @Command
    @NotifyChange("workListModel")
    public void deleteWork(@BindingParam("work") Work work) {
        workListModel.remove(work);
        workDAO.delete(workDAO.get(work.getId()));
    }

    @Command
    @NotifyChange({"workListModel", "canWork", "work"})
    public void addNewWork() {
        workListModel.add(work);
        countAmount(work);
        workDAO.saveOrUpdate(work);
        work = null;
        canWork = false;
    }

    /* materials */

    @Command
    @NotifyChange("canMaterial")
    public void onChangeVisibilityAddMaterial() {
        if (!canMaterial) {
            material = new Material(this.task);
        }
        canMaterial = !canMaterial;
    }

    @Command
    @NotifyChange({"materialListModel", "material", "canMaterial"})
    public void addNewMaterial() {
        materialListModel.add(material);
        countAmount(material);
        materialDAO.saveOrUpdate(material);
        material = null;
        canMaterial = false;
    }

    @Command
    @NotifyChange("materialListModel")
    public void updateNewMaterial(@BindingParam("material") Material material) {
        countAmount(material);
        material.setAmount(material.getQuantity() * material.getUnitPrice());
        materialDAO.saveOrUpdate(material);
    }

    @Command
    @NotifyChange("materialListModel")
    public void deleteMaterial(@BindingParam("material") Material material) {
        materialListModel.remove(material);
        materialDAO.delete(materialDAO.get(material.getId()));
    }

    private void countAmount(Element element) {
        if (element.getQuantity() != null && element.getUnitPrice() != null) {
            element.setAmount(element.getQuantity() * element.getUnitPrice());
        }
    }

    @Command
    @NotifyChange({"canToTemplate", "template", "templateWorkAndMaterialList"})
    public void addTemplate() {
        template.setWorkList(workListModel);
        template.setMaterialList(materialListModel);
        templateDAO.saveOrUpdate(template);
        canToTemplate = !canToTemplate;
        template = null;
    }

    @Command
    @NotifyChange({"canToTemplate", "template"})
    public void addNewTemplate() {
        if (!canToTemplate) {
            this.template = new Template();
        }
        canToTemplate = !canToTemplate;
    }

    @Command
    @NotifyChange({"canFromTemplate", "template", "templateWorkAndMaterialList"})
    public void findNewTemplate() {
        if (!canFromTemplate) {
            List<Template> templateList = templateDAO.findAllTemplates();
            templateWorkAndMaterialList = new ListModelList<>(templateList);
        }
        canFromTemplate = !canFromTemplate;
    }

    @Command
    @NotifyChange({"canFromTemplate", "templateWorkAndMaterialList", "workListModel", "materialListModel"})
    public void findTemplate(@BindingParam("template") Template template) {
        List<Work> workList = workDAO.findByTemplate(template);
        workListModel = new ListModelList<>(workList);

        List<Material> materialList = materialDAO.findByTemplate(template);
        materialListModel = new ListModelList<>(materialList);
    }

}
