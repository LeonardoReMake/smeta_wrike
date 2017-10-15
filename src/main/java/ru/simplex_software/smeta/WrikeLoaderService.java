package ru.simplex_software.smeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.simplex_software.smeta.dao.CityDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.WrikeTaskDaoImpl;
import ru.simplex_software.smeta.model.City;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.util.ImportInfo;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.binarySearch;

@Service
public class WrikeLoaderService {
    private static final Logger LOG = LoggerFactory.getLogger(WrikeLoaderService.class);

    @Resource
    private WrikeTaskDaoImpl wrikeTaskDAO;

    @Resource
    private TaskDAO taskDAO;

    @Resource
    private CityDAO cityDAO;

    public ImportInfo loadNewTasks() {
        ImportInfo importInfo = new ImportInfo();
        List<Task> taskInDb = taskDAO.findAllTasks();

        LocalDateTime lastCreationDate = taskInDb.get(0).getCreatedDate();
        LOG.info("Last updated task date: "+lastCreationDate);
        List<Task> newTasks = wrikeTaskDAO.findTasksStartDate(lastCreationDate.minusMonths(2));

        if (newTasks.size() != 0) {
            LOG.info("find "+newTasks.size()+" in last 2 months");
            importInfo.setImportedTaskCount(newTasks.size());
            int createdTasksCount = 0;
            int updatedTaskCount = 0;
            for (Task task : newTasks) {
                task.setPath(wrikeTaskDAO.findPathForTask(task));
                String log = parseTaskTitle(task);
                if (log.length() > 0) {
                    importInfo.addNotParsedTask(task, log);
                }

                Task taskToUpdate = taskDAO.findByWrikeId(task.getWrikeId());
                if (taskToUpdate == null) {
                    taskDAO.create(task);
                    createdTasksCount++;
                } else {
                    task.setId(taskToUpdate.getId());
                    taskDAO.merge(task);
                    updatedTaskCount++;
                }
            }
            LOG.info("Count of created tasks: "+createdTasksCount);
            LOG.info("Count of updated tasks: "+updatedTaskCount);
        } else {
            LOG.info("No new tasks");
        }
        return importInfo;
    }

    private String parseTaskTitle(Task task) {
        StringBuilder log = new StringBuilder();
        String rawTitle = task.getName();

        String[] parts = rawTitle.split(" ");

        if (parts.length < 4) {
            LOG.warn("could not parse string: "+rawTitle);
            return "Невозможно выделить данные из названия задачи.";
        }

        int index = 0;
        String number = "";

        if (parts[index].contains(":")) {
            number = parts[index].substring(0, parts[index].length()-1);
            index++;
        } else {
//            log.append("Невозможно выделить номер задачи. Отсутсвует ':' .\n");
        }

        String city = parts[index];
        if (city.equals("Заявка")) {
            LOG.warn("could not parse string: "+rawTitle);
            return "Невозможно выделить данные из названия задачи.";
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
        } else {
//            log.append("Невозможно выделить id заявки. Отсутствует 'INC'.\n");
        }

        List<City> cities = cityDAO.findCityForName(city);
        City taskCity;
        if (cities == null || cities.isEmpty()) {
            log.append("Город ").append(city).append(" не найден в имеющемся списке городов. \n");
            List<City> citiesForPath = cityDAO.findCityForName(task.getPath());
            if (!citiesForPath.isEmpty()) {
                log.append("Город задачи определен по папке wrike: ").append(citiesForPath.get(0).getName()).append(". ");
                taskCity = citiesForPath.get(0);
            } else {
                taskCity = new City();
                taskCity.setName(task.getPath());
                cityDAO.create(taskCity);
                log.append("Город ").append(task.getPath()).append(" добавлен в базу данных. \n");
            }
        } else {
            taskCity = cities.get(0);
        }

        task.setShopName(shop.toString());
        task.setCity(taskCity);
        task.setOrderNumber(id);
        return log.toString();
    }

    private boolean isShop(String str) {
        return !(str.equals("OCS") || str.equals("BCS")
                || str.equals("RCS") || str.equals("FO") || str.startsWith("INC"));
    }

}
