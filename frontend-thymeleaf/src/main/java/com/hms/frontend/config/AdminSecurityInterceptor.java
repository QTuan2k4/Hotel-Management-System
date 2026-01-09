package com.hms.frontend.config;

import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        SessionAuth auth = null;
        
        if (session != null) {
            Object authObj = session.getAttribute("AUTH");
            if (authObj instanceof SessionAuth) {
                auth = (SessionAuth) authObj;
            }
        }

        // Check if user is logged in and has ADMIN role
        if (auth == null || !auth.isLoggedIn() || !auth.isAdmin()) {
            // Store error message in session for display after redirect
            if (session == null) {
                session = request.getSession(true);
            }
            session.setAttribute("adminAccessError", "Admin access required. Please login with admin account.");
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
