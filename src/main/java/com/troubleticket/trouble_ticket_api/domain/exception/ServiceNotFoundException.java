package com.troubleticket.trouble_ticket_api.domain.exception;

public class ServiceNotFoundException extends RuntimeException {

    private final Long serviceId;

    public ServiceNotFoundException(Long serviceId) {
        super("Service with ID " + serviceId + " not found, not active, or not in tenant scope");
        this.serviceId = serviceId;
    }

    public Long getServiceId() {
        return serviceId;
    }
}
