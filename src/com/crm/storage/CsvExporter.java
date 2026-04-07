package com.crm.storage;

import com.crm.model.Client;
import java.io.*;
import java.util.List;

public class CsvExporter {

    public void exportToCsv(List<Client> clients, String filename) throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        String fullPath = "data/" + (filename.endsWith(".csv") ? filename : filename + ".csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath))) {
            writer.println("id,name,email,phone,company");
            for (Client client : clients) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        client.getId(),
                        escapeCsv(client.getName()),
                        escapeCsv(client.getEmail()),
                        escapeCsv(client.getPhone()),
                        escapeCsv(client.getCompany())
                );
            }
        }
        System.out.println("Exported " + clients.size() + " clients to " + fullPath);
    }

    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}