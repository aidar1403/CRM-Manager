package com.crm.service;

import java.util.Scanner;

public class AuthService {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "user123";

    private String currentUser;
    private String currentRole;
    private boolean isAuthenticated;

    public AuthService() {
        isAuthenticated = false;
    }

    // Для консольной версии
    public boolean login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n═══════════════════════════════════");
        System.out.println("         LOGIN SYSTEM");
        System.out.println("═══════════════════════════════════");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        return login(username, password);
    }

    // Для GUI версии
    public boolean login(String username, String password) {
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            currentUser = username;
            currentRole = "ADMIN";
            isAuthenticated = true;
            System.out.println("\n✅ Login successful! Welcome, " + username + " (ADMIN)");
            return true;
        } else if (username.equals(USER_USERNAME) && password.equals(USER_PASSWORD)) {
            currentUser = username;
            currentRole = "USER";
            isAuthenticated = true;
            System.out.println("\n✅ Login successful! Welcome, " + username + " (USER)");
            return true;
        } else {
            System.out.println("\n❌ Invalid username or password!");
            return false;
        }
    }

    public boolean isAdmin() { return isAuthenticated && currentRole.equals("ADMIN"); }
    public boolean isUser() { return isAuthenticated && currentRole.equals("USER"); }
    public boolean isAuthenticated() { return isAuthenticated; }
    public String getCurrentUser() { return currentUser; }
    public String getCurrentRole() { return currentRole; }

    public void logout() {
        currentUser = null;
        currentRole = null;
        isAuthenticated = false;
        System.out.println("✅ Logged out successfully!");
    }
}