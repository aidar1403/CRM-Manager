package com.crm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client extends Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    private int id;
    private String company;
    private List<String> interactions;


    public Client(String name, String email, String phone, String company) {
        super(name, email, phone);
        this.id = nextId++;
        this.company = company;
        this.interactions = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }

    public String getCompany() { return company; }
    public List<String> getInteractions() { return interactions; }

    public void setCompany(String company) {
        this.company = (company != null) ? company : "";
    }
    public void addInteraction(String interaction) {
        if (interaction != null && !interaction.trim().isEmpty()) {
            interactions.add(interaction);
        }
    }
    @Override
    public String getDisplayInfo() {
        return "ID: " + id + " | " + getName() + " | " + getEmail() + " | " + company;
    }
}