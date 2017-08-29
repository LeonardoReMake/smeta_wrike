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
import ru.simplex_software.smeta.model.Work;

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

    private Task task;

    private Work work;

    private boolean checkTask;

    private ListModelList<Task> taskListModel;

    public TaskViewModel() {
    }

    public ListModelList<Task> getTaskListModel() {
        return taskListModel;
    }

    public void setTaskListModel(ListModelList<Task> taskListModel) {
        this.taskListModel = taskListModel;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isCheckTask() {
        return checkTask;
    }

    public void setCheckTask(boolean checkTask) {
        this.checkTask = checkTask;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    @Init
    public void init() {
        wrikeLoaderService.loadNewTasks();
        List<Task> taskList = taskDAO.findAllTasks();
        taskListModel = new ListModelList<>(taskList);

        for (Task task : taskListModel) {
            if (task.getWorks().isEmpty() && task.getMaterials().isEmpty()) {
                checkTask = true;
            } else {
                checkTask = false;
            }
        }

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
