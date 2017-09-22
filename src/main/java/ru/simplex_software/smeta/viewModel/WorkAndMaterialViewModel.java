package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.TemplateDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.model.Element;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;
import ru.simplex_software.smeta.model.template.Template;
import ru.simplex_software.zkutils.DetachableModel;

import java.util.Iterator;
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

    private ListModelList<Work> taskWorkListModel;

    private ListModelList<Material> taskMaterialListModel;

    private ListModelList<Template> templateListModel;

    private Work work;

    private Material material;

    private Template template = new Template();

    private boolean canWork;

    private boolean canMaterial;

    private boolean canToTemplate;

    private boolean canFromTemplate;

    private boolean fromTemplate;

    private boolean toTemplate;

    private Window window;

    public boolean isFromTemplate() {
        return fromTemplate;
    }

    public void setFromTemplate(boolean fromTemplate) {
        this.fromTemplate = fromTemplate;
    }

    public boolean isToTemplate() {
        return toTemplate;
    }

    public void setToTemplate(boolean toTemplate) {
        this.toTemplate = toTemplate;
    }

    private int countTemplate;

    public int getCountTemplate() {
        return countTemplate;
    }

    public void setCountTemplate(int countTemplate) {
        this.countTemplate = countTemplate;
    }

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

    public ListModelList<Material> getTaskMaterialListModel() {
        return taskMaterialListModel;
    }

    public void setTaskMaterialListModel(ListModelList<Material> taskMaterialListModel) {
        this.taskMaterialListModel = taskMaterialListModel;
    }

    public ListModelList<Work> getTaskWorkListModel() {
        return taskWorkListModel;
    }

    public ListModelList<Template> getTemplateListModel() {
        return templateListModel;
    }

    public void setTemplateListModel(ListModelList<Template> templateListModel) {
        this.templateListModel = templateListModel;
    }

    public void setTaskWorkListModel(ListModelList<Work> taskWorkListModel) {
        this.taskWorkListModel = taskWorkListModel;
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
    public void init(@ExecutionArgParam("task") Task task, @ContextParam(ContextType.VIEW) Window window) {
        List<Work> workList = workDAO.findByTask(task);
        taskWorkListModel = new ListModelList<>(workList);

        List<Material> materialList = materialDAO.findByTasks(task);
        taskMaterialListModel = new ListModelList<>(materialList);

        List<Template> templateList = templateDAO.findAllTemplates();
        templateListModel = new ListModelList<>(templateList);

        this.task = task;
        this.window = window;
    }

    @Command
    @NotifyChange("canWork")
    public void onChangeVisibilityAddWork() {
        if (!canWork) {
            work = new Work();
            work.setTask(task);
        }
        canWork = !canWork;
    }

    @Command
    @NotifyChange("taskWorkListModel")
    public void updateNewWork(@BindingParam("work") Work work) {
        countAmount(work);
        workDAO.saveOrUpdate(work);
    }

    @Command
    @NotifyChange("taskWorkListModel")
    public void deleteWork(@BindingParam("work") Work work) {
        taskWorkListModel.remove(work);
        workDAO.delete(workDAO.get(work.getId()));
    }

    @Command
    @NotifyChange({"taskWorkListModel", "canWork", "work"})
    public void addNewWork() {
        taskWorkListModel.add(work);
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
            material = new Material();
            material.setTask(this.task);
        }
        canMaterial = !canMaterial;
    }

    @Command
    @NotifyChange({"taskMaterialListModel", "material", "canMaterial"})
    public void addNewMaterial() {
        taskMaterialListModel.add(material);
        countAmount(material);
        materialDAO.saveOrUpdate(material);
        material = null;
        canMaterial = false;
    }

    @Command
    @NotifyChange("taskMaterialListModel")
    public void updateNewMaterial(@BindingParam("material") Material material) {
        countAmount(material);
        materialDAO.saveOrUpdate(material);
    }

    @Command
    @NotifyChange("taskMaterialListModel")
    public void deleteMaterial(@BindingParam("material") Material material) {
        taskMaterialListModel.remove(material);
        materialDAO.delete(materialDAO.get(material.getId()));
    }

    // save template
    @Command
    @NotifyChange({"canToTemplate", "fromTemplate", "canFromTemplate"})
    public void addTemplate() {
        Template newTemplate = new Template();
        for (Work work : taskWorkListModel) {
            Work newTemplateWork = new Work();
            newTemplateWork.setQuantity(work.getQuantity());
            newTemplateWork.setUnitPrice(work.getUnitPrice());
            newTemplateWork.setUnits(work.getUnits());
            newTemplateWork.setAmount(work.getAmount());
            newTemplateWork.setName(work.getName());
            newTemplateWork.setTemplate(newTemplate);
            newTemplate.getWorkList().add(newTemplateWork);
        }

        for (Material material : taskMaterialListModel) {
            Material newTemplateMaterial = new Material();
            newTemplateMaterial.setQuantity(material.getQuantity());
            newTemplateMaterial.setUnitPrice(material.getUnitPrice());
            newTemplateMaterial.setUnits(material.getUnits());
            newTemplateMaterial.setAmount(material.getAmount());
            newTemplateMaterial.setName(material.getName());
            newTemplateMaterial.setTemplate(newTemplate);
            newTemplate.getMaterialList().add(newTemplateMaterial);
        }

        newTemplate.setName(template.getName());
        templateDAO.create(newTemplate);

        fromTemplate = !fromTemplate;
        canToTemplate = !canToTemplate;
        canFromTemplate = !canFromTemplate;

        work = null;
        material = null;
    }

    // create new template
    @Command
    @NotifyChange({"canToTemplate", "template", "canFromTemplate", "fromTemplate"})
    public void addNewTemplate() {
        if (!canToTemplate) {
            template = new Template();
        }
        canToTemplate = !canFromTemplate;
        canFromTemplate = !canFromTemplate;
        fromTemplate = !fromTemplate;
    }

    @Command
    @NotifyChange({"canFromTemplate", "templateListModel", "canToTemplate", "toTemplate"})
    public void cancelFindingTemplate() {
        canToTemplate = !canFromTemplate;
        canFromTemplate = !canFromTemplate;
        toTemplate = !toTemplate;
    }

    @Command
    @NotifyChange({"canFromTemplate", "templateListModel", "fromTemplate", "canToTemplate", "canFromTemplate"})
    public void cancelAddingTemplate() {
        fromTemplate = !fromTemplate;
        canToTemplate = !canToTemplate;
        canFromTemplate = !canFromTemplate;
    }

    @Command
    @NotifyChange({"canFromTemplate", "templateListModel", "canToTemplate", "toTemplate"})
    public void findNewTemplate() {
        if (!canFromTemplate) {
            List<Template> templateList = templateDAO.findAllTemplates();
            templateListModel = new ListModelList<>(templateList);
        }

        toTemplate = !toTemplate;
        canFromTemplate = !canFromTemplate;
    }

    @Command
    public void closeWindow() {
        Events.postEvent("onClose", window, null);
    }

    // применить шаблон
    @Command
    @NotifyChange({"canFromTemplate", "canToTemplate", "taskWorkListModel",
                    "taskMaterialListModel", "fromTemplate", "toTemplate"})
    public void findTemplate() {
        int countTemplate = this.countTemplate;

        for (int i = 0; i < countTemplate; i++) {
            // template's works and materials
            List<Work> templateWorkList = workDAO.findByTemplate(template);
            List<Material> templateMaterialList = materialDAO.findByTemplate(template);

            Iterator<Work> workIterator = templateWorkList.iterator();
            Iterator<Material> materialIterator = templateMaterialList.iterator();

            removeElementsOfListIteratorForWorks(workIterator);
            createNewWork(templateWorkList);

            removeElementsOfListIteratorForMaterial(materialIterator);
            createNewMaterial(templateMaterialList);
        }

        canToTemplate = !canFromTemplate;
        canFromTemplate = !canFromTemplate;
        toTemplate = !toTemplate;
    }

    private void countAmount(Element element) {
        if (element.getQuantity() != null && element.getUnitPrice() != null) {
            element.setAmount(element.getQuantity() * element.getUnitPrice());
        }
    }

    private void updateAmount(Work work) {
        work.setAmount(work.getQuantity() * work.getUnitPrice());
    }

    private void removeElementsOfListIteratorForWorks(Iterator<? extends Element> iterator) {
        boolean isRemove;

        while (iterator.hasNext()) {
            Element outerWork = iterator.next();
            isRemove = true;
            for (Work innerWork : taskWorkListModel) {
                if (innerWork.getName().equals(outerWork.getName())
                        && innerWork.getUnitPrice().equals(outerWork.getUnitPrice()) && isRemove) {
                    innerWork.setQuantity(innerWork.getQuantity() + outerWork.getQuantity());
                    innerWork.setAmount(innerWork.getQuantity() * innerWork.getUnitPrice());
                    taskWorkListModel.notifyChange(innerWork);
                    workDAO.saveOrUpdate(innerWork);
                    isRemove = false;
                }
            }
            if (!isRemove)
                iterator.remove();
        }

    }

    private void removeElementsOfListIteratorForMaterial(Iterator<? extends Element> iterator) {
        boolean isRemove;

        while (iterator.hasNext()) {
            Element outerMaterial = iterator.next();
            isRemove = true;
            for (Material innerMaterial : taskMaterialListModel) {
                if (innerMaterial.getName().equals(outerMaterial.getName())
                        && innerMaterial.getUnitPrice().equals(outerMaterial.getUnitPrice()) && isRemove) {
                    innerMaterial.setQuantity(innerMaterial.getQuantity() + outerMaterial.getQuantity());
                    innerMaterial.setAmount(innerMaterial.getQuantity() * innerMaterial.getUnitPrice());
                    taskMaterialListModel.notifyChange(innerMaterial);
                    materialDAO.saveOrUpdate(innerMaterial);
                    isRemove = false;
                }
            }
            if (!isRemove)
                iterator.remove();
        }
    }

    private void createNewWork(List<Work> workList) {
        if (!workList.isEmpty()) {
            for (Work work : workList) {
                Work newWork = new Work();
                newWork.setQuantity(work.getQuantity());
                newWork.setUnitPrice(work.getUnitPrice());
                newWork.setUnits(work.getUnits());
                newWork.setAmount(work.getAmount());
                newWork.setName(work.getName());
                newWork.setTask(task);
                taskWorkListModel.add(newWork);
                workDAO.create(newWork);
            }
        }
    }

    private void createNewMaterial(List<Material> materialList) {
        if (!materialList.isEmpty()) {
            for (Material material : materialList) {
                Material newMaterial = new Material();
                newMaterial.setQuantity(material.getQuantity());
                newMaterial.setUnitPrice(material.getUnitPrice());
                newMaterial.setUnits(material.getUnits());
                newMaterial.setAmount(material.getAmount());
                newMaterial.setName(material.getName());
                newMaterial.setTask(task);
                taskMaterialListModel.add(newMaterial);
                materialDAO.create(newMaterial);
            }
        }
    }

}

