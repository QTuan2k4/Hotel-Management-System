package com.hms.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private Services services = new Services();
    private Security security = new Security();

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public static class Services {
        private String auth;
        private String rooms;
        private String bookings;
        private String invoices;
        private String payments;
        private String reports;
        private String notify;

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }

        public String getRooms() {
            return rooms;
        }

        public void setRooms(String rooms) {
            this.rooms = rooms;
        }

        public String getBookings() {
            return bookings;
        }

        public void setBookings(String bookings) {
            this.bookings = bookings;
        }

        public String getInvoices() {
            return invoices;
        }

        public void setInvoices(String invoices) {
            this.invoices = invoices;
        }

        public String getPayments() {
            return payments;
        }

        public void setPayments(String payments) {
            this.payments = payments;
        }

        public String getReports() {
            return reports;
        }

        public void setReports(String reports) {
            this.reports = reports;
        }

        public String getNotify() {
            return notify;
        }

        public void setNotify(String notify) {
            this.notify = notify;
        }
    }

    public static class Security {
        private String internalKey;

        public String getInternalKey() {
            return internalKey;
        }

        public void setInternalKey(String internalKey) {
            this.internalKey = internalKey;
        }
    }
}
