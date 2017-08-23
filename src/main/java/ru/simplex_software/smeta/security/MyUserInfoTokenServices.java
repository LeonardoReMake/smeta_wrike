package ru.simplex_software.smeta.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import ru.simplex_software.smeta.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyUserInfoTokenServices implements ResourceServerTokenServices {
    private final static Log logger = LogFactory.getLog(MyUserInfoTokenServices.class);

    private final String userInfoEndpointUrl;
    private final String clientId;
    private OAuth2RestOperations restTemplate;
    private String tokenType = "Bearer";

    public MyUserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setRestTemplate(OAuth2RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = this.getMap(this.userInfoEndpointUrl, accessToken);
        if (map.containsKey("error")) {
            if (logger.isDebugEnabled()) {
                logger.debug("userinfo returned error: " + map.get("error"));
            }

            throw new InvalidTokenException(accessToken);
        } else {
            return this.extractAuthentication(map);
        }
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        Object principal = this.getPrincipal(map);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        OAuth2Request request = new OAuth2Request(null, this.clientId, null, true, null, null, null, null, null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        token.setDetails(map);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, token);
        oAuth2Authentication.setAuthenticated(true);
        return oAuth2Authentication;
    }

    private Object getPrincipal(Map<String, Object> map) {
        ArrayList contacts = (ArrayList) map.get("data");
        Map data = (Map)contacts.get(0);
        User user = new User();
        user.setFirstName((String) data.get("firstName"));
        user.setLastName((String) data.get("lastName"));
        user.setId((String) data.get("id"));
        return user;
    }

    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    private Map<String, Object> getMap(String path, String accessToken) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting user info from: " + path);
        }

        try {
            OAuth2RestOperations restTemplate = this.restTemplate;
            if (restTemplate == null) {
                BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
                resource.setClientId(this.clientId);
                restTemplate = new OAuth2RestTemplate(resource);
            }

            OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext().getAccessToken();
            if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);
                token.setTokenType(this.tokenType);
                restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }

            Map map = restTemplate.getForEntity(path, Map.class, new Object[0]).getBody();
            Map<String, Object> resultMap = new HashMap<>();
            for (Object entry : map.entrySet()) {
                if (entry instanceof Map.Entry) {
                    Map.Entry mapEntry = (Map.Entry) entry;
                    resultMap.put((String)mapEntry.getKey(), mapEntry.getValue());
                }
            }
            return resultMap;
        } catch (Exception var6) {
            logger.warn("Could not fetch user details: " + var6.getClass() + ", " + var6.getMessage());
            return Collections.singletonMap("error", "Could not fetch user details");
        }
    }
}

