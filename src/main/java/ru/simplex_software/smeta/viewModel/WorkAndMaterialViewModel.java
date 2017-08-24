package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
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


}
