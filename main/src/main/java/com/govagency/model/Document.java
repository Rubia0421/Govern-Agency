package com.govagency.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Document {
    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    private String documentId;
    private String citizenId;
    private Status status;
    private LocalDateTime uploadTime;

    public Document(String documentId, String citizenId) {
        this.documentId = documentId;
        this.citizenId = citizenId;
        this.status = Status.PENDING;
        this.uploadTime = LocalDateTime.now();
        
    }

    public String getId() {
        return documentId;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Document ID: " + documentId +
                ", Citizen ID: " + citizenId +
                ", Uploaded at: " + uploadTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                ) +", Status: " + (status != null ? status : "Not Reviewed");
    }
}