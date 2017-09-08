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
import ru.simplex_software.smeta.excel.ReportCreator;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private WrikeLoaderService wrikeLoaderService;

    private ListModelList<Task> taskListModel;

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
    public void getWorksAndMaterials(@BindingParam("task") Task task) {
        Map<String, Task> tasksMap = new HashMap<>();

        tasksMap.put("task", task);

        Executions.createComponents("/dialog.zul", null, tasksMap);
    }

    @Command
    /* создаем отсчет */
    public void createReport() throws IOException {

        final List<Long> totalAmountListOfWorks = new ArrayList<>();
        final List<Long> totalAmountListOfMaterials = new ArrayList<>();

        final ReportCreator reportCreator = new ReportCreator();

        final int startColumnOfWork = 0;
        final int startColumnOfMaterial = 5;

        long amountTotalForWorks = 0;
        long amountTotalForMaterials = 0;

        reportCreator.addFields();

        for (Task task : taskDAO.findAllTasks()) {
            reportCreator.setTask(task);

            Iterator<Work> workIterator = workDAO.findByTask(task).iterator();
            Iterator<Material> materialIterator = materialDAO.findByTasks(task).iterator();

            while (workIterator.hasNext() && materialIterator.hasNext()) {

                amountTotalForWorks = 0;
                amountTotalForMaterials = 0;

                Material material = materialIterator.next();
                Work work = workIterator.next();

                reportCreator.setWorkOrMaterial(work);
                reportCreator.setWorkOrMaterial(material);

                amountTotalForWorks += work.getAmount();
                amountTotalForMaterials += material.getAmount();
                reportCreator.newRow();
                reportCreator.setIndexColumn(startColumnOfWork);
                reportCreator.setTotalAmountForWorks(amountTotalForWorks);
                reportCreator.setIndexColumn(2 * startColumnOfMaterial - 1);
                reportCreator.setTotalAmountForMaterials(amountTotalForMaterials);
                reportCreator.newRow();
            }


            totalAmountListOfWorks.add(amountTotalForWorks);
            totalAmountListOfMaterials.add(amountTotalForMaterials);

        }

        reportCreator.newRow();

        reportCreator.setTotalAmountAllElements(totalAmountListOfWorks, "Всего работы: ");
        long totalAmountOfWorks = reportCreator.getTotalAmountAllElements();

        reportCreator.setIndexColumn(startColumnOfMaterial);
        long totalAmountOfMaterials = reportCreator.getTotalAmountAllElements();

        reportCreator.newRow();

        reportCreator.setTotalAmountAllElements(totalAmountListOfWorks, "Всего материалы: ");

        reportCreator.newRow();
        long totalAmounts = totalAmountOfWorks + totalAmountOfMaterials;
        reportCreator.allAmountOfEstimate(totalAmountOfWorks + totalAmountOfMaterials);

        reportCreator.newRow();
        reportCreator.allOnEstimate(totalAmounts);
        reportCreator.newRow();
        reportCreator.setFooterEstimate();

        reportCreator.build();
    }

}
