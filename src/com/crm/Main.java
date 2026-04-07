package com.crm;

import com.crm.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("CRM Manager started!");


        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}