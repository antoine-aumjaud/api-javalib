package fr.aumjaud.antoine.services.common.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.security.SecurityHelper;

@Configuration
public class SpringProducer {
    @Bean
    public SecurityHelper creatSecurityHelper() {
        return new SecurityHelper();
    }

    @Bean
    public PropertyHelper createPropertyHelper() {
        return new PropertyHelper();
    }
}
