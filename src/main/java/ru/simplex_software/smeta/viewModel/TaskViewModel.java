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
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.dao.WrikeTaskDaoImpl;
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
    private WorkDAO workDAO;

    @WireVariable
    private MaterialDAO materialDAO;

    @WireVariable
    private WrikeTaskDaoImpl wrikeTaskDao;

    @WireVariable
    private WrikeLoaderService wrikeLoaderService;

    private ListModelList<Task> taskListModel = new ListModelList<>();

    public TaskViewModel() {}

    public ListModelList<Task> getTaskListModel() {
        return taskListModel;
    }

    public void setTaskListModel(ListModelList<Task> taskListModel) {
        this.taskListModel = taskListModel;
    }

    @Init
    public void init() {
        List<Task> taskList = taskDAO.findAllTasks();
        for (Task task : taskList) {
            task.setFilled(!(task.getWorks().isEmpty() &&
                             task.getMaterials().isEmpty()));
        }
        taskListModel = new ListModelList<>(taskList);
    }

    @Command
    public void loadNewTasks() {
        wrikeLoaderService.loadNewTasks();

        final List<Task> taskList = taskDAO.findAllTasks();
        taskListModel.clear();
        taskListModel.addAll(taskList);
    }

    @Command
    public void getWorksAndMaterials(@BindingParam("task") Task task) {
        Map<String, Task> tasksMap = new HashMap<>();

        tasksMap.put("task", task);

        Executions.createComponents("/dialog.zul", null, tasksMap);
    }

}
