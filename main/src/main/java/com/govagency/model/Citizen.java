package com.govagency.model;

public class Citizen {
    private final String id;
    private String name;
    private String email;
    private String number;

    public Citizen(String id, String name, String number, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Citizen ID: " + id + "\nName: " + name + "\nEmail: " + email + "\nNumber: " + number; 
    }
}