package fr.aumjaud.antoine.services.common.server.springboot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.aumjaud.antoine.services.common.security.SecurityHelper;

@Service
public class SecureFilter implements Filter {

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = ((HttpServletRequest) req);
        if (request.getRequestURI().startsWith("/secure/")) {
            String requestSecureKey = request.getHeader(SecurityHelper.SECURE_KEY_NAME);
            if (requestSecureKey == null)
                requestSecureKey = request.getParameter(SecurityHelper.SECURE_KEY_NAME);

            securityHelper.checkAccess(applicationConfig.getSecureKey(), requestSecureKey);
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}