package com.crm.ui;
import com.crm.storage.CsvExporter;
import com.crm.service.CRMService;
import com.crm.storage.FileStorage;
import com.crm.model.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConsoleUI {
    private CRMService service;
    private FileStorage storage;
    private Scanner scanner;

    public ConsoleUI(){
        service = new CRMService();
        storage = new FileStorage();
        scanner = new Scanner(System.in);

        List<Client> loaded = storage.load();
        if (!loaded.isEmpty()) {
            for (Client c : loaded) {
                service.addClient(c.getName(),c.getEmail(),c.getPhone(),c.getCompany());
            }
            System.out.println("Loaded "+loaded.size()+"clients into memory.");
        }
    }

    public void start() {
        System.out.println("\t\t\t\tCRM MANAGER\t\t\t\t");
        System.out.println("\t\t\tManagement System\t\t\t");

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = getIntInput("\nChoose option: ");
            switch (choice) {
                case 1:
                    addClient();
                    break;
                case 2:
                    viewAllClients();
                    break;
                case 3:
                    findClientById();
                    break;
                case 4:
                    updateClient();
                    break;
                case 5:
                    deleteClient();
                    break;
                case 6:
                    addInteraction();
                    break;
                case 7:
                    viewInteractions();
                    break;
                case 8:
                    showStatistics();
                    break;
                case 9:
                    exportData();      // НОВЫЙ
                    break;
                case 10:
                    importData();      // НОВЫЙ
                    break;
                case 0:
                    running = false;
                    saveAndExit();
                    break;
                default:
                    System.out.println("Invalid option. Please choose 0-10.");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n========== CRM MANAGER ==========");
        System.out.println("1. Add New Client");
        System.out.println("2. View All Clients");
        System.out.println("3. Find Client by ID");
        System.out.println("4. Update Client");
        System.out.println("5. Delete Client");
        System.out.println("6. Add Interaction (Note)");
        System.out.println("7. View Client Interactions");
        System.out.println("8. Statistics");
        System.out.println("9. Export to CSV");
        System.out.println("10. Import from CSV");
        System.out.println("0. Save & Exit");
        System.out.println("==================================");
    }

    private void addClient() {
        System.out.println("\nAdd New Client");

        String name = getStringInput("Name: ", true);
        String email = getStringInput("Email: ",true);
        String phone = getStringInput("Phone (optional): ", false);
        String company = getStringInput("Company (optional): ", false);

        service.addClient(name, email, phone, company);
    }
    private void viewAllClients() {
        List<Client>clients = service.getAllClients();

        if (clients.isEmpty()) {
            System.out.println("\nNo clients found. Add some clients first.");
            return;
        }

System.out.println("\t\t\tAll Clients\t\t\t");
        System.out.println("ID | Name | Email| Company | Interactions");

        for (Client c : clients) {
            System.out.println(c.getId()+" | "+
                    c.getName()+ " | "+
                    c.getEmail()+ " | "+
                    c.getCompany()+" | "+ c.getInteractions().size());
        }
        System.out.println("Total: "+clients.size()+" clients(s)");
    }

    private void findClientById() {
        int id = getIntInput("Enter client ID: ");
    Client client = service.findClientById(id);

    if (client == null) {
        System.out.println("Client with ID " + id + " not found.");
        return;
    }

    System.out.println("\n\t\t\tClient Details\t\t\t");
    System.out.println("ID:           "+client.getId());
    System.out.println("Name:         "+client.getName());
    System.out.println("Email:        "+client.getEmail());
    System.out.println("Phone:        "+(client.getPhone().isEmpty()? "N/A" : client.getPhone()));
    System.out.println("Company:      "+(client.getCompany().isEmpty()? "N/A" : client.getCompany()));
    System.out.println("Interactions: "+client.getInteractions().size());
    }

    private void updateClient() {
        int id = getIntInput("Enter client ID to update: ");
        Client client = service.findClientById(id);

        if (client == null) {
            System.out.println("Client with ID "+id+" not found.");
            return;
        }

        System.out.println("\nUpdating client: "+client.getDisplayInfo());
        System.out.println("(Press Enter to keep current value)");

        String name = getStringInput("New name [" + client.getName()+"]: :",false);
        String email = getStringInput("New email ["+ client.getEmail() + "]: ", false);
        String phone = getStringInput("New phone ["+ client.getPhone()+"]: ", false);
        String company = getStringInput("New company ["+client.getCompany()+ "]: ",false);

        service.updateClient(id,
                name.isEmpty()? null: name,
                email.isEmpty()? null : email,
                phone.isEmpty()? null : phone,
                company.isEmpty()? null : company);
        System.out.println("Client updated Successfully!");
    }

    private void deleteClient() {
        int id = getIntInput("Enter client ID to delete: ");
        Client client = service.findClientById(id);

        if (client==null) {
            System.out.println("Client with ID" + id + "not found.");
        return;
        }

        System.out.println("\nClient to delete: "+client.getDisplayInfo());
        String confirm = getStringInput("Are you sure? (y/n): ",true);

        if (confirm.equalsIgnoreCase("y")) {
            service.deleteClient(id);
            System.out.println("Client deleted successfully!");
        }else{
            System.out.println("Delete cancelled");
        }
    }

    private void addInteraction() {
        int id = getIntInput("Enter client ID: ");
        Client client = service.findClientById(id);

        if (client == null) {
            System.out.println("Client with ID "+id+" not found");
            return;
        }

        System.out.println("\nClient: "+client.getDisplayInfo());
        String note = getStringInput("Interaction note: ",true);

        service.addInteraction(id,note);
        System.out.println("Interaction added successfully!");
    }

    private void viewInteractions() {
        int id = getIntInput("Enter client ID: ");
        Client client = service.findClientById(id);

        if (client == null) {
            System.out.println("Client with ID" + id + "not found");
            return;
        }

        List <String>interactions = service.getClientInteractions(id);

    if(interactions.isEmpty()) {
        System.out.println("\nNo interactions found for "+client.getName());
        return;
    }
    System.out.println("\nInteractions For "+client.getName().toUpperCase()+" .");
    for (int i = 0; i<interactions.size();i++) {
        System.out.println((i+1)+". "+interactions.get(i));
    }
    System.out.println("Total: "+interactions.size()+" interaction(s)");
    }

    private void showStatistics() {
        int clientCount = service.getClientCount();
        int totalInteractions = 0;

        for (Client c : service.getAllClients()) {
            totalInteractions += c.getInteractions().size();
        }

        System.out.println("\nStatistics");
        System.out.println("Total Clients: "+clientCount);
        System.out.println("Total Interactions: "+totalInteractions);
        if (clientCount>0) {
            double avg = (double)totalInteractions/clientCount;
            System.out.println("Avg per Client: "+avg);
        }
    }

    private void saveAndExit() {
        List<Client> clients = service.getAllClients();
        storage.save(clients);
        System.out.println("\nData saved. Goodbye!");
    }

    private void exportData() {
        System.out.println("\nExport to CSV");
        System.out.print("Enter filename (e.g., my_clients): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "clients_export";
        }

        CsvExporter exporter = new CsvExporter();
        try {
            List<Client> clients = service.getAllClients();
            if (clients.isEmpty()) {
                System.out.println("No clients to export.");
                return;
            }
            exporter.exportToCsv(clients, filename);
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    private void importData() {
        System.out.println("\n--- Import from CSV ---");
        System.out.print("Enter filename to import (e.g., clients.csv): ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Filename cannot be empty.");
            return;
        }

        String fullPath = "data/" + (filename.endsWith(".csv") ? filename : filename + ".csv");
        File file = new File(fullPath);

        if (!file.exists()) {
            System.out.println("File not found: " + fullPath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line = reader.readLine(); // пропускаем заголовки
            int count = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String name = parts[1].replace("\"", "").trim();
                    String email = parts[2].replace("\"", "").trim();
                    String phone = parts.length > 3 ? parts[3].replace("\"", "").trim() : "";
                    String company = parts.length > 4 ? parts[4].replace("\"", "").trim() : "";

                    service.addClient(name, email, phone, company);
                    count++;
                }
            }
            System.out.println("Imported " + count + " clients from " + fullPath);
        } catch (IOException e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }

    private String getStringInput(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!required || !input.isEmpty()) {
                return input;
            }
            System.out.println("This field is required. Please enter a value.");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            }catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
