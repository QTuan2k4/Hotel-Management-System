package com.hms.gateway.proxy;

import com.hms.gateway.config.GatewayProperties;
import org.springframework.stereotype.Component;

@Component
public class RouteResolver {

    private final GatewayProperties props;

    public RouteResolver(GatewayProperties props) {
        this.props = props;
    }

    public String resolveBaseUrl(String path) {
        // path always starts with /api/
        String[] parts = path.split("/");
        // ["", "api", "{p2}", "{p3}", ...]
        if (parts.length < 3)
            return null;
        String p2 = parts[2];

        if ("admin".equals(p2) || "internal".equals(p2)) {
            if (parts.length < 4)
                return null;
            String module = parts[3];
            return mapModule(module);
        }
        return mapModule(p2);
    }

    private String mapModule(String module) {
        return switch (module) {
            case "auth", "users" -> props.getServices().getAuth();
            case "rooms" -> props.getServices().getRooms();
            case "bookings" -> props.getServices().getBookings();
            case "invoices" -> props.getServices().getInvoices();
            case "payments" -> props.getServices().getPayments();
            case "reports" -> props.getServices().getReports();
            case "notify", "notifications" -> props.getServices().getNotify();
            default -> null;
        };
    }
}
