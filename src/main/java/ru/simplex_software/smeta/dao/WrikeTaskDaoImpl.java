package ru.simplex_software.smeta.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.WrikeObject;

import java.util.List;

public class WrikeTaskDaoImpl {
    private final static String API_ADDRESS = "https://www.wrike.com/api/v3";

    private OAuth2RestTemplate restTemplate;

    @Autowired
    public WrikeTaskDaoImpl(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Task> findTasks() {
        String url = API_ADDRESS + "/tasks";

        WrikeObject result = restTemplate.getForObject(url, WrikeObject.class);

        return result.getData();
    }

}
