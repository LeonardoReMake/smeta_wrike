package ru.simplex_software.smeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.dao.WrikeTaskDaoImpl;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WrikeLoaderService {
    private static final Logger LOG = LoggerFactory.getLogger(WrikeLoaderService.class);

    @Resource
    private WrikeTaskDaoImpl wrikeTaskDAO;

    @Resource
    private TaskDAO taskDAO;

    @Resource
    private WorkDAO workDAO;

    @Resource
    private MaterialDAO materialDAO;

    public void loadNewTasks() {
        List<Task> taskInDb = taskDAO.findAllTasks();
        if (taskInDb.size() == 0) {
            List<Task> tasks = wrikeTaskDAO.findTasks();

            for (Task task : tasks) {
                taskDAO.saveOrUpdate(task);
                createNewWorks(task);
                createNewMaterials(task);
            }
        }
    }

    public void createNewWorks(Task task) {
        for (int i = 0; i < 5; i++) {
            Work work = new Work("Замена замка мебельного",
                                  "шт",
                                 3d,
                                150d,
                                 450d, task);
            task.getWorks().add(work);
        }
    }

    public void createNewMaterials(Task task) {
        for (int i = 0; i < 5; i++) {
            Material material = new Material("Замена замка мебельного",
                                   "шт",
                                  3d,
                                 150d,
                                  450d, task);
            task.getMaterials().add(material);
        }
    }


}
