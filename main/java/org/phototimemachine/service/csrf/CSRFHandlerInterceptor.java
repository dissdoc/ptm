package org.phototimemachine.service.csrf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CSRFHandlerInterceptor  extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CSRFHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        if (!request.getMethod().equalsIgnoreCase("POST") || !request.getMethod().equalsIgnoreCase("DELETE") ||
                !request.getMethod().equalsIgnoreCase("PUT")) {
            return true;
        }

        String sessionToken = CSRFTokenManager.getTokenForSession(request.getSession());
        String requestToken = CSRFTokenManager.getTokenFromRequest(request);

        if (sessionToken.equals(requestToken)) {
            return true;
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRF value");
            logger.warn("CSRF Attack! Session Token = " + sessionToken + ", but Request Token = " + requestToken);
            return false;
        }
    }
}
