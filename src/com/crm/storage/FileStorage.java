package com.crm.storage;

import com.crm.model.Client;
import java.io.*;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "clients.dat";
    private static final String FILE_PATH = DATA_DIR + File.separator + FILE_NAME;

    public FileStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
           dir.mkdirs();
           System.out.println("Created data directory: "+DATA_DIR);
        }
    }

    public void save(List<Client> clients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(clients);
            System.out.println("Saved "+clients.size()+" clients to file");
        }catch (IOException e) {
            System.out.println("Error saving data: "+e.getMessage());
        }
    } 
    @SuppressWarnings("unchecked")
    public List<Client> load() {
        File file = new File(FILE_PATH);

        if(!file.exists()) {
            System.out.println("No existing data file found. Starting fresh.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Client> loaded = (List<Client>) ois.readObject();
            System.out.println("Loaded " + loaded.size() + " clients from file");
            return loaded;
        }catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: "+ e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean dataExists() {
        return new File(FILE_PATH).exists();
    }

    public String getFilePath() {
        return FILE_PATH;
    }
}