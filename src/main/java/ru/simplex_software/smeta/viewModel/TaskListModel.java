package ru.simplex_software.smeta.viewModel;

import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.event.ListDataEvent;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.TaskFilterImplDAO;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;

import java.util.ArrayList;
import java.util.List;

public class TaskListModel extends AbstractListModel<Task> {

    private static final int PAGE_SIZE = 10;

    private TaskFilter currentFilter;

    private List<Long> taskIds = new ArrayList<>(PAGE_SIZE);

    // текущее кол-во
    private int length = -1;

    private TaskDAO taskDAO;

    private TaskFilterImplDAO taskFilterImplDAO;

    private int lastPageInList = 0;

    TaskListModel(TaskFilter filter, TaskDAO taskDAO, TaskFilterImplDAO taskFilterImplDAO) {
        this.taskDAO = taskDAO;
        this.taskFilterImplDAO = taskFilterImplDAO;
        currentFilter = filter;
    }

    @Override
    public int getSize() {
        if (length == -1 && taskIds.size() == 0) {
            length = taskFilterImplDAO.countTasksByFilter(currentFilter).intValue();
        }
        return length;
    }

    @Override
    public Task getElementAt(int index) {
        // если список пуст или номер страницы поменялся
        // загружаем элементы из базы
        if (taskIds.size() == 0 || lastPageInList != index / PAGE_SIZE) {
            taskIds = taskFilterImplDAO.findTaskIdsByFilter(currentFilter, index, PAGE_SIZE);
            lastPageInList = index / PAGE_SIZE;
        }
        int listIndex = index - (index / PAGE_SIZE) * PAGE_SIZE;
        return taskDAO.get(taskIds.get(listIndex));
    }

    void refresh(TaskFilter filter) {
        currentFilter = filter;
        taskIds.clear();
        length = -1;
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
}
