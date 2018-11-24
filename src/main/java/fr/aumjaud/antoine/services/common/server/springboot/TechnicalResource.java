package fr.aumjaud.antoine.services.common.server.springboot;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.logger.ApplicationLogger;
import fr.aumjaud.antoine.services.common.security.NoAccessException;
import fr.aumjaud.antoine.services.common.security.WrongRequestException;
import fr.aumjaud.antoine.services.common.server.ServerInfo;

@RestController
public class TechnicalResource {
    private final static String COMMON_CONFIG_FILENAME = "common.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalResource.class);

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

    @ExceptionHandler(NoAccessException.class)
    public final ResponseEntity<String> handleNoAccessExceptions(NoAccessException e, WebRequest request) {
        ApplicationLogger.getInstance().accessIssue(e.getMessage());
        return new ResponseEntity<>("no access: " + e.getClientMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WrongRequestException.class)
    public final ResponseEntity<String> handleWrongRequestExceptions(WrongRequestException e, WebRequest request) {
        ApplicationLogger.getInstance().parameterIssue(e.getMessage());
        return new ResponseEntity<>("wrong request: " + e.getClientMessage(), HttpStatus.PRECONDITION_FAILED);
    }
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception e, WebRequest request) {
        LOGGER.error("Unhandled exception returned to client", e);
        return new ResponseEntity<>("internal server error, see server logs", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
