package ru.simplex_software.smeta.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.WrikeObject;

import java.io.IOException;
import java.util.List;
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
        String url = API_ADDRESS + "/tasks";

        WrikeObject result = restTemplate.getForObject(url, WrikeObject.class);

        return result.getData();
    }

}
