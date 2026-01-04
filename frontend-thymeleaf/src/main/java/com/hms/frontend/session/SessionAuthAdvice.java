package com.hms.frontend.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionAuthAdvice {

    @ModelAttribute("sessionAuth")
    public SessionAuth sessionAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : null;
    }
}
