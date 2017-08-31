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
import java.time.LocalDateTime;
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

        List<Task> newTasks;

        if (taskInDb.size() != 0) {
            LocalDateTime lastCreationDate = taskInDb.get(0).getCreatedDate();
            LOG.info("Last updated task date: "+lastCreationDate);
            newTasks = wrikeTaskDAO.findTasksStartDate(lastCreationDate.plusSeconds(1));
        } else {
            newTasks = wrikeTaskDAO.findTasks();
        }

        if (newTasks.size() != 0) {
            LOG.info("find "+newTasks.size()+" new tasks");
            for (Task task : newTasks) {
                parseTaskTitle(task);
                task.setPath(wrikeTaskDAO.findPathForTask(task));
                createNewWorks(task);
                createNewMaterials(task);
                taskDAO.saveOrUpdate(task);
            }
        } else {
            LOG.info("No new tasks");
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

    private void parseTaskTitle(Task task) {
        String rawTitle = task.getName();

        String[] parts = rawTitle.split(" ");

        if (parts.length < 4) {
            LOG.warn("could not parse string: "+rawTitle);
            return;
        }

        int index = 0;
        String number = "";

        if (parts[index].contains(":")) {
            number = parts[index].substring(0, parts[index].length()-1);
            index++;
        }

        String city = parts[index];
        if (city.equals("Заявка")) {
            LOG.warn("could not parse string: "+rawTitle);
            return;
        }
        index++;

        StringBuilder shop = new StringBuilder();

        int i = index;
        for (; i < parts.length && isShop(parts[i]); i++) {
            shop.append(parts[i]).append(" ");
        }

        while (i < parts.length && !parts[i].startsWith("INC")) { i++; }

        String system = "";
        if (!isShop(parts[i-1])) {
            system = parts[i-1];
        }

        String id = "";
        if (i < parts.length && parts[i].startsWith("INC")) {
            id = parts[i];
        }

        task.setShopName(shop.toString());
        task.setCity(city);
        task.setOrderNumber(id);
    }

    private boolean isShop(String str) {
        return !(str.equals("OCS") || str.equals("BCS")
                || str.equals("RCS") || str.equals("FO") || str.startsWith("INC"));
    }

}
