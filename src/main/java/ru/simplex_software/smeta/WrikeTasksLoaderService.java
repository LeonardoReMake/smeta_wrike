package ru.simplex_software.smeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.WrikeTaskDaoImpl;
import ru.simplex_software.smeta.model.Task;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WrikeTasksLoaderService {
    private static final Logger LOG = LoggerFactory.getLogger(WrikeTasksLoaderService.class);

    @Resource
    private WrikeTaskDaoImpl wrikeTaskDAO;

    @Resource
    private TaskDAO taskDAO;

    public void loadNewTasks() {
        List<Task> taskInDb = taskDAO.findAllTasks();
        if (taskInDb.size() == 0) {
            List<Task> tasks = wrikeTaskDAO.findTasks();

            for (Task task : tasks) {
                taskDAO.saveOrUpdate(task);
            }
        }
    }
}
