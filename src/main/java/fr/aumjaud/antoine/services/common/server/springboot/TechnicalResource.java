package fr.aumjaud.antoine.services.common.server.springboot;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.server.ServerInfo;

@RestController
public class TechnicalResource {
    private final static String COMMON_CONFIG_FILENAME = "common.properties";

    @Autowired
    private ApplicationConfig applicationConfig;

    private final Properties commonProperties;

    @Autowired
    public TechnicalResource(PropertyHelper propertyHelper) {
        commonProperties = propertyHelper.loadProperties(COMMON_CONFIG_FILENAME);
    }

    @RequestMapping("/hi")
    public String hello() {
        return "hello";
    }

    @RequestMapping(value="/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ServerInfo info() {
        return new ServerInfo(commonProperties);
    }

    @RequestMapping("/secure/reloadConfig")
    public String reloadConfig() {
        applicationConfig.reload();
        return "done";
    }
}
