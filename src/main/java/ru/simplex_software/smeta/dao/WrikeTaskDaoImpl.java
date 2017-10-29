package ru.simplex_software.smeta.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.WrikeObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WrikeTaskDaoImpl {
    private static final Logger LOG = LoggerFactory.getLogger(WrikeTaskDaoImpl.class);

    private static String API_ADDRESS;
    public static String WRIKE_ACCOUNT;
    {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("oauth.properties"));
        API_ADDRESS = properties.getProperty("oauth.apiAddress");
        WRIKE_ACCOUNT = properties.getProperty("wrike.account");
    }

    private OAuth2RestTemplate restTemplate;

    @Autowired
    public WrikeTaskDaoImpl(OAuth2RestTemplate restTemplate) throws IOException {
        this.restTemplate = restTemplate;
    }

    public List<Task> findTasks() {

        String url = API_ADDRESS + "/accounts/"+ WRIKE_ACCOUNT +"/tasks?fields=[\"parentIds\",\"superParentIds\"]";

        WrikeObject result = restTemplate.getForObject(url, WrikeObject.class);

        return result.getData();
    }

    public List<Task> findTasksStartDate(LocalDateTime startDate) {
        String url = API_ADDRESS + "/tasks?fields=[\"parentIds\",\"superParentIds\"]&createdDate={createdDate}";
        startDate = startDate.plusSeconds(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String createdDate = "{\"start\":\"" + formatter.format(startDate) + "\"}";

        WrikeObject result = restTemplate.getForObject(url, WrikeObject.class, createdDate);

        return result.getData();
    }

    public ManagerCity findManagerCityNameForTask(Task task) {
        LOG.info("Start finding manager for task: "+task.getName());

        List<String> path = new LinkedList<>();
        String parentFolderId = task.getParentIds().get(0);
        String title = findFolderTitleForId(parentFolderId);

        while (!title.equals("")) {
            path.add(0, title);
            parentFolderId = findParentFolderId(parentFolderId);
            title = findFolderTitleForId(parentFolderId);
        }

        if (path.size() < 2) {
            return null;
        }

        LOG.info("The manager name: {}", path.get(0));
        LOG.info("The city name: {}", path.get(1));
        return new ManagerCity(path.get(0), path.get(1));
    }

    private String findFolderTitleForId(String id) {
//        LOG.info("Find title for folder with id: {}", id);
        // проверка, является ли папка рут
        if (id.endsWith("777")) // id root папки заканчивается на несколько 7
            return "";
        String url = API_ADDRESS + "/folders/"+id;

        Map response = restTemplate.getForObject(url, Map.class);

        List<Map> folders = (List<Map>) response.get("data");

        return (String) folders.get(0).get("title");
    }

    private String findParentFolderId(String folderId) {
//        LOG.info("Find title for folder with id: {}", folderId);
        // проверка, является ли папка рут
        if (folderId.endsWith("777")) // id root папки заканчивается на несколько 7
            return "";

        String url = API_ADDRESS + "/folders/"+folderId;

        Map response = restTemplate.getForObject(url, Map.class);

        List<Map> folders = (List<Map>) response.get("data");

        return (String) ((List)folders.get(0).get("parentIds")).get(0);
    }

    // обертка, которая хранит имя менеджера и город задачи
    public static class ManagerCity {
        public String manager;

        public String city;

        ManagerCity(String manager, String city) {
            this.manager = manager;
            this.city = city;
        }
    }
}
