package fr.aumjaud.antoine.services.common.server.springboot;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.aumjaud.antoine.services.common.security.NoAccessException;
import fr.aumjaud.antoine.services.common.security.SecurityHelper;

@Service
public class SecureFilter implements Filter {
    @NotNull
    @Value("${app.name}")
    private String apiName;

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = ((HttpServletRequest) req);
        HttpServletResponse response = ((HttpServletResponse) res);

        //Add CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        //Manage OPTIONS method from browsers
        if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setHeader("Access-Control-Max-Age", "86400");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        //Check Secure info
        if(request.getRequestURI().startsWith("/secure/")) {
            String configSecureToken = applicationConfig.getSecureKey();
            String requestAuthorization = request.getHeader(SecurityHelper.AUTHORIZATION_HEADER);
            if(requestAuthorization != null) {
                if(requestAuthorization.startsWith("Basic")) {
                    String requestSecureKeyAuthHeader = new String(Base64.getDecoder().decode(requestAuthorization.substring(6 /* "Basic".length */)), "UTF-8");
                    requestSecureKeyAuthHeader = requestSecureKeyAuthHeader.substring(0, requestSecureKeyAuthHeader.length() - 1); //remove ":" separator between username and password
                    securityHelper.checkSecureKeyAccess(configSecureToken, requestSecureKeyAuthHeader);
                    chain.doFilter(req, res);
                    return;
                }
                else if(requestAuthorization.startsWith("Bearer"))  {
                    String requestTokenAuthHeader = requestAuthorization.substring(7 /* "Bearer".length */);
                    securityHelper.checkJWTAccess(requestTokenAuthHeader, apiName);
                    chain.doFilter(req, res);
                    return;
                }
            }

            String requestSecureKeyHeader = request.getHeader(SecurityHelper.SECURE_KEY_NAME);
            if(requestSecureKeyHeader != null) {
                securityHelper.checkSecureKeyAccess(configSecureToken, requestSecureKeyHeader);
                chain.doFilter(req, res);
                return;
            }
            String requestSecureKeyParam = request.getParameter(SecurityHelper.SECURE_KEY_NAME);
            if (requestSecureKeyParam != null) {
                securityHelper.checkSecureKeyAccess(configSecureToken, requestSecureKeyParam);
                chain.doFilter(req, res);
                return;            
            }

            throw new NoAccessException("no credentials", "Try to access to API without credentials");
        }

        //Pass through
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}