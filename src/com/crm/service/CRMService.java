package com.crm.service;

import java.util.List;
import java.util.ArrayList;
import com.crm.model.Client;
public class CRMService {
    private List<Client> clients;

    public CRMService() {
        clients = new ArrayList<>();
    }

    public void addClient(String name, String email, String phone, String company) {
        Client client = new Client(name, email, phone, company);
        clients.add(client);
        System.out.println("Client added: " + client.getDisplayInfo());
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    public Client findClientById(int id) {
        for (Client c : clients) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public boolean updateClient(int id, String name, String email, String phone, String company) {
        Client client = findClientById(id);
        if (client == null) {
            System.out.println("Client with ID " + id + " not found");
            return false;
        }
        if (name != null && !name.trim().isEmpty()) {
            client.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            client.setEmail(email);
        }
        if (phone != null) {
            client.setPhone(phone);
        }
        if (company != null) {
            client.setCompany(company);
        }
        System.out.println("Client updated: " + client.getDisplayInfo());
        return true;
    }

    public boolean deleteClient(int id) {
        Client client = findClientById(id);
        if (client == null) {
            System.out.println("Client with ID " + id + " not found");
            return false;
        }
        boolean removed = clients.remove(client);
        if (removed) {
            System.out.println("Client deleted: " + client.getDisplayInfo());
        }
        return removed;
    }

    public boolean addInteraction(int clientId,String interaction){
        Client client = findClientById(clientId);
        if (client == null){
            System.out.println("Client with ID "+clientId+" not found");
            return false;
        }

        client.addInteraction(interaction);
        System.out.println("Interaction added for: "+client.getName());
        return true;
    }

    public List<String> getClientInteractions(int clientId){
        Client client = findClientById(clientId);
        if (client == null){
            return new ArrayList<>();
        }
        return client.getInteractions();
    }

    public int getClientCount() {
        return clients.size();
    }

    public void setClients(List<Client>loadedClients) {
        this.clients = new ArrayList<>(loadedClients);

        int maxId = 0;
        for (Client c : clients) {
            if (c.getId()>maxId) {
                maxId = c.getId();
            }
        }
    }
}