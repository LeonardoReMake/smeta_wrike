package ru.simplex_software.smeta.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.WrikeObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WrikeTaskDaoImpl {
    private static String API_ADDRESS;

    private OAuth2RestTemplate restTemplate;

    @Autowired
    public WrikeTaskDaoImpl(OAuth2RestTemplate restTemplate) throws IOException {
        this.restTemplate = restTemplate;
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("oauth.properties"));
        API_ADDRESS = properties.getProperty("oauth.apiAddress");
    }

    public List<Task> findTasks() {
        String url = API_ADDRESS + "/tasks?fields=[\"parentIds\",\"superParentIds\"]";

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

    public String findPathForTask(Task task) {
        String path = "";

        if (task.getParentIds() == null) {
            return "";
        }

        String url = API_ADDRESS + "/folders/"+task.getParentIds().get(0)+"/folders";

        Map response = restTemplate.getForObject(url, Map.class);

        List<Map> folders = (List<Map>) response.get("data");

        for (Map entity : folders) {
            if (entity.get("id").equals(task.getParentIds().get(0))) {
                return (String) entity.get("title");
            }
        }

        return path;
    }
}
