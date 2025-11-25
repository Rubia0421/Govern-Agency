package com.govagency.model;

public class ServiceRequest {
    public enum Status {
        REQUESTED, PROCESSING, COMPLETED, REJECTED
    }

    private final String id;
    private final String citizenId;
    private final String serviceType;
    private final String description;
    private Status status;

    public ServiceRequest(String id, String citizenId, String serviceType, String description) {
        this.id = id;
        this.citizenId = citizenId;
        this.serviceType = serviceType;
        this.description = description;
        this.status = Status.REQUESTED;
    }

    public String getId() {
        return id;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
               "Service Type: " + serviceType + "\n" +
               "Description: " + description + "\n" +
               "Status: " + status + "\n";
    }
}