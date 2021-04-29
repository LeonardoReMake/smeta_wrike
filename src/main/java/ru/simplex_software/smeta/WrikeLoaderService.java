package ru.simplex_software.smeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.simplex_software.smeta.dao.CityDAO;
import ru.simplex_software.smeta.dao.ManagerDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.WrikeTaskDaoImpl;
import ru.simplex_software.smeta.model.City;
import ru.simplex_software.smeta.model.Manager;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.util.ImportInfo;

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
    private CityDAO cityDAO;

    @Resource
    private ManagerDAO managerDAO;

    public ImportInfo loadNewTasks() {
        int taskAmount = 0;

        ImportInfo importInfo = new ImportInfo();
        List<Task> taskInDb = taskDAO.findAllTasks();

        List<Task> newTasks;
        if (taskInDb.size() != 0) {
            LocalDateTime lastCreationDate = taskInDb.get(0).getCreatedDate();
            LOG.info("Last updated task date: " + lastCreationDate);
            newTasks = wrikeTaskDAO.findTasksStartDate(lastCreationDate.minusMonths(2));
        } else {
            LOG.info("Load all tasks");
            newTasks = wrikeTaskDAO.findTasks();
        }

        if (newTasks.size() != 0) {
            int createdTasksCount = 0;
            int updatedTaskCount = 0;

            for (Task task : newTasks) {
                WrikeTaskDaoImpl.ManagerCity managerCity = wrikeTaskDAO.findManagerCityNameForTask(task);

                if (managerCity == null) {
                    continue;
                }

                String managerName = managerCity.manager;
                String cityName = managerCity.city;

                taskAmount++;
                task.setManager(getManagerForName(managerName));
                task.setCity(getCityForName(cityName));
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

            importInfo.setImportedTaskCount(taskAmount);
            LOG.info("Count of created tasks: "+createdTasksCount);
            LOG.info("Count of updated tasks: "+updatedTaskCount);
        } else {
            LOG.info("No new tasks");
        }

        return importInfo;
    }

    private String parseTaskTitle(Task task) {
        String rawTitle = task.getName();

        String[] parts = rawTitle.split(" ");

        if (parts.length < 4) {
            LOG.warn("could not parse string: "+rawTitle);
            return "Невозможно выделить данные из названия задачи.";
        }

        int index = 0;

        if (parts[index].contains(":")) {
            index++;
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

        String id = "";
        if (i < parts.length && parts[i].startsWith("INC")) {
            id = parts[i];
        }

        task.setShopName(shop.toString());
        task.setOrderNumber(id);
        return "";
    }

    private boolean isShop(String str) {
        return !(str.equals("OCS") || str.equals("BCS")
                || str.equals("RCS") || str.equals("FO") || str.startsWith("INC"));
    }

    private Manager getManagerForName(String name) {
        Manager manager = managerDAO.findMangerForName(name);
        if (manager == null) {
            LOG.info("Could not find manager with name {}", name);
            LOG.info("Creating new manager with name {}", name);
            manager = new Manager();
            manager.setName(name);
            managerDAO.create(manager);
        }

        return manager;
    }

    private City getCityForName(String cityName) {
        List<City> cityList = cityDAO.findCityForName(cityName);
        City city;

        if (cityList == null || cityList.isEmpty()) {
            LOG.info("Could not find city for name: {}", cityName);
            LOG.info("Creating new city for name: {}", cityName);
            city = new City();
            city.setName(cityName);
            cityDAO.create(city);
        } else {
            city = cityList.get(0);
        }

        return city;
    }

}
