
package fr.aumjaud.antoine.services.common.server.springboot;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.security.SecurityHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ApplicationConfig {
    @NotNull
    @Value("${app.name}.properties")
    private String name;

    @Autowired
    private PropertyHelper propertyHelper;

    private Properties properties;

    public String getSecureKey() {
        return properties.getProperty(SecurityHelper.SECURE_KEY_NAME);
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    @PostConstruct
    public void reload() {
        properties = propertyHelper.loadProperties(name);
    }
}
