package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import ru.simplex_software.smeta.WrikeLoaderService;
import ru.simplex_software.smeta.dao.CityDAO;
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.TaskFilterDAO;
import ru.simplex_software.smeta.dao.TaskFilterImplDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.dao.WrikeTaskDaoImpl;
import ru.simplex_software.smeta.model.City;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;
import ru.simplex_software.smeta.model.Work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    @WireVariable
    private CityDAO cityDAO;

    @WireVariable
    private TaskFilterImplDAO taskFilterImplDAO;

    @WireVariable
    private TaskFilterDAO taskFilterDAO;

    // текущий фильтр
    private TaskFilter filter;

    // список город, которые будут отображаться в выпадающем списке в complete боксе
    private List<City> cities = new ArrayList<>();

    private TaskListModel taskListModel;

    public TaskViewModel() {}

    public List<City> getCities() {
        return cities;
    }

    public TaskFilter getFilter() {
        return filter;
    }

    public void setFilter(TaskFilter filter) {
        this.filter = filter;
    }

    public TaskListModel getTaskListModel() {
        return taskListModel;
    }

    public void setTaskListModel(TaskListModel taskListModel) {
        this.taskListModel = taskListModel;
    }

    @AfterCompose
    @NotifyChange("taskListModel")
    public void init() {
        List<Task> taskList = taskDAO.findAllTasks();

        List<TaskFilter> filters = taskFilterDAO.findAllFilters();
        if (filters.isEmpty()) {
            filter = new TaskFilter();
            taskFilterDAO.create(filter);
        } else {
            filter = filters.get(0);
        }

        for (Task task : taskList) {
            addAmountForTask(task);
        }

        taskListModel = new TaskListModel(filter, taskDAO, taskFilterImplDAO);
        refreshList();

        cities = cityDAO.findAll();
    }

    @Command
    @NotifyChange("taskListModel")
    public void loadNewTasks() {
        wrikeLoaderService.loadNewTasks();
        refreshList();
    }

    @Command
    @NotifyChange("taskListModel")
    public void getWorksAndMaterials(@BindingParam("task") Task task) {
        Map<String, Task> tasksMap = new HashMap<>();
        tasksMap.put("task", task);

        Component components = Executions.createComponents("/dialog.zul", null, tasksMap);
        components.addEventListener("onClose", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Task persistentTask = taskDAO.get(task.getId());
                addAmountForTask(persistentTask);
                taskDAO.saveOrUpdate(persistentTask);
                refreshList();
            }
        });
    }

    @Command
    @NotifyChange({"taskListModel"})
    public void applyFilter() {
        taskFilterDAO.saveOrUpdate(filter);
        refreshList();
    }

    @Command
    @NotifyChange("filter")
    public void clearFilter() {
        filter.setCity(null);
        filter.setStartDate(null);
        filter.setEndDate(null);
        filter.setCities(new HashSet<>());
        taskFilterDAO.saveOrUpdate(filter);
    }

    @Command
    public void redirectToPrices() {
        Executions.sendRedirect("/price.zul");
    }

    private void refreshList() {
        taskListModel.refresh(filter);
    }

    private void addAmountForTask(Task task) {
        double amountTask = 0;
        for (Work work : task.getWorks()) {
            amountTask += work.getAmount();
        }

        for (Material material : task.getMaterials()) {
            amountTask += material.getAmount();
        }

        task.setAmount(amountTask);
    }

}
