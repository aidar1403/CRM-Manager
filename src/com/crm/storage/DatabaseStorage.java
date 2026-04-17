package com.crm.storage;

import com.crm.model.Client;
import java.sql.*;
import java.util.*;

public class DatabaseStorage {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/user/IdeaProjects/CRM-Manager/data/crm.db";

    public DatabaseStorage() {
        createTables();
        System.out.println("✅ Connected to database: " + DB_URL);
    }

    private void createTables() {
        String clientsTable = """
            CREATE TABLE IF NOT EXISTS clients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                phone TEXT,
                company TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String interactionsTable = """
            CREATE TABLE IF NOT EXISTS interactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                client_id INTEGER NOT NULL,
                note TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
            )
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(clientsTable);
            stmt.execute(interactionsTable);
            System.out.println("✅ Database tables ready");
        } catch (SQLException e) {
            System.out.println("❌ Table creation error: " + e.getMessage());
        }
    }

    public void saveAll(List<Client> clients) {
        String sql = "INSERT OR REPLACE INTO clients (id, name, email, phone, company) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Client client : clients) {
                pstmt.setInt(1, client.getId());
                pstmt.setString(2, client.getName());
                pstmt.setString(3, client.getEmail());
                pstmt.setString(4, client.getPhone());
                pstmt.setString(5, client.getCompany());
                pstmt.executeUpdate();

                saveInteractions(conn, client.getId(), client.getInteractions());
            }
            System.out.println("✅ Saved " + clients.size() + " clients to database");
        } catch (SQLException e) {
            System.out.println("❌ Save error: " + e.getMessage());
        }
    }

    private void saveInteractions(Connection conn, int clientId, List<String> interactions) throws SQLException {
        String deleteSql = "DELETE FROM interactions WHERE client_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, clientId);
            pstmt.executeUpdate();
        }

        String insertSql = "INSERT INTO interactions (client_id, note) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (String note : interactions) {
                pstmt.setInt(1, clientId);
                pstmt.setString(2, note);
                pstmt.executeUpdate();
            }
        }
    }

    public List<Client> loadAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, name, email, phone, company FROM clients ORDER BY id";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String company = rs.getString("company");

                Client client = new Client(name, email, phone, company);
                client.setId(id);

                List<String> interactions = loadInteractions(conn, id);
                for (String note : interactions) {
                    client.addInteraction(note);
                }

                clients.add(client);
            }
            System.out.println("✅ Loaded " + clients.size() + " clients from database");
        } catch (SQLException e) {
            System.out.println("❌ Load error: " + e.getMessage());
        }
        return clients;
    }

    private List<String> loadInteractions(Connection conn, int clientId) throws SQLException {
        List<String> interactions = new ArrayList<>();
        String sql = "SELECT note FROM interactions WHERE client_id = ? ORDER BY created_at";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                interactions.add(rs.getString("note"));
            }
        }
        return interactions;
    }
}