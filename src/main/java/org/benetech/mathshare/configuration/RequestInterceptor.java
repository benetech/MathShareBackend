package org.benetech.mathshare.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        String partnerToken = request.getHeader("X-Partner-Token");
        String partnerCode = request.getHeader("X-Partner-Code");
        if (partnerToken == null || (partnerCode != null && !partnerCode.isEmpty())) {
            return true;
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
            // Above code will send a 401 with no response body.
            // If you need a 401 view, do a redirect instead of
            // returning false.
            // response.sendRedirect("/401"); // assuming you have a handler mapping for 401

        }
    }
}
