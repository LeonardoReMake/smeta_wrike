package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import ru.simplex_software.smeta.WrikeLoaderService;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class TaskViewModel {

    private static Logger LOG = LoggerFactory.getLogger(TaskViewModel.class);

    @WireVariable
    private TaskDAO taskDAO;

    @WireVariable
    private WrikeLoaderService wrikeLoaderService;

    private ListModelList<Task> taskListModel;

    public ListModelList<Task> getTaskListModel() {
        return taskListModel;
    }

    public void setTaskListModel(ListModelList<Task> taskListModel) {
        this.taskListModel = taskListModel;
    }

    @Init
    public void init() {
        wrikeLoaderService.loadNewTasks();
        List<Task> taskList = taskDAO.findAllTasks();
        taskListModel = new ListModelList<>(taskList);
    }

    @Command
    public String getButtonLabel(@BindingParam("task") Task task){
        if (task.isFilled()){
            return "Редактировать";
        } else {
            return "Заполнить";
        }
    }

    @Command
    public void getWorksAndMaterials(@BindingParam("task") Task task) {
        Map<String, Task> tasksMap = new HashMap<>();

        tasksMap.put("task", task);

        Executions.createComponents("/test1.zul", null, tasksMap);
    }

}
