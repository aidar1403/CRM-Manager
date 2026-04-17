package com.crm.gui;

import com.crm.model.Client;
import com.crm.service.CRMService;
import com.crm.service.AuthService;
import com.crm.storage.DatabaseStorage;
import com.crm.storage.CsvExporter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;

public class CrmGui extends Application {
    private CRMService service;
    private DatabaseStorage storage;
    private AuthService authService;
    private ObservableList<Client> clientList;

    private TextField tfName, tfEmail, tfPhone, tfCompany;
    private TextArea taInteractions;
    private ListView<Client> lvClients;
    private Label lblStatus;
    private Label lblId;

    @Override
    public void start(Stage primaryStage) {
        service = new CRMService();
        storage = new DatabaseStorage();
        authService = new AuthService();

        // Загрузка данных из БД
        List<Client> loaded = storage.loadAll();
        for (Client c : loaded) {
            service.addClient(c.getName(), c.getEmail(), c.getPhone(), c.getCompany());
        }
        clientList = FXCollections.observableArrayList(service.getAllClients());

        // Логин
        if (!showLoginDialog()) {
            System.exit(0);
        }

        primaryStage.setTitle("CRM Manager - " + authService.getCurrentUser() +
                " (" + authService.getCurrentRole() + ")");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // ЛЕВАЯ ПАНЕЛЬ
        lvClients = new ListView<>(clientList);
        lvClients.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + " - " + item.getName() + " (" + item.getCompany() + ")");
                }
            }
        });
        lvClients.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> showClientDetails(selected));

        VBox leftPanel = new VBox(10, new Label("📋 КЛИЕНТЫ:"), lvClients);
        leftPanel.setPrefWidth(250);

        // ЦЕНТРАЛЬНАЯ ПАНЕЛЬ
        GridPane formPanel = new GridPane();
        formPanel.setHgap(10);
        formPanel.setVgap(10);
        formPanel.setPadding(new Insets(10));

        formPanel.add(new Label("ID:"), 0, 0);
        lblId = new Label("");
        formPanel.add(lblId, 1, 0);

        formPanel.add(new Label("Имя:*"), 0, 1);
        tfName = new TextField();
        tfName.setPromptText("Введите имя");
        formPanel.add(tfName, 1, 1);

        formPanel.add(new Label("Email:*"), 0, 2);
        tfEmail = new TextField();
        tfEmail.setPromptText("user@example.com");
        formPanel.add(tfEmail, 1, 2);

        formPanel.add(new Label("Телефон:"), 0, 3);
        tfPhone = new TextField();
        tfPhone.setPromptText("+996 XXX XX XX");
        formPanel.add(tfPhone, 1, 3);

        formPanel.add(new Label("Компания:"), 0, 4);
        tfCompany = new TextField();
        tfCompany.setPromptText("Название компании");
        formPanel.add(tfCompany, 1, 4);

        // КНОПКИ
        HBox buttonPanel = new HBox(10);
        Button btnAdd = new Button("➕ Добавить");
        Button btnUpdate = new Button("✏️ Обновить");
        Button btnDelete = new Button("🗑️ Удалить");
        Button btnRefresh = new Button("🔄 Обновить");
        Button btnExport = new Button("📁 Экспорт CSV");
        Button btnImport = new Button("📂 Импорт CSV");

        boolean isAdmin = authService.isAdmin();
        btnAdd.setDisable(!isAdmin);
        btnUpdate.setDisable(!isAdmin);
        btnDelete.setDisable(!isAdmin);
        btnExport.setDisable(!isAdmin);
        btnImport.setDisable(!isAdmin);

        btnAdd.setOnAction(e -> addClient());
        btnUpdate.setOnAction(e -> updateClient());
        btnDelete.setOnAction(e -> deleteClient());
        btnRefresh.setOnAction(e -> refreshList());
        btnExport.setOnAction(e -> exportToCsv());
        btnImport.setOnAction(e -> importFromCsv());

        buttonPanel.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnRefresh, btnExport, btnImport);
        formPanel.add(buttonPanel, 0, 5, 2, 1);

        // ЗАМЕТКИ
        formPanel.add(new Label("📝 ЗАМЕТКИ:"), 0, 6);
        taInteractions = new TextArea();
        taInteractions.setPrefRowCount(5);
        taInteractions.setEditable(false);
        formPanel.add(taInteractions, 1, 6);

        Button btnAddNote = new Button("➕ Добавить заметку");
        btnAddNote.setOnAction(e -> addNote());
        btnAddNote.setDisable(!isAdmin);
        formPanel.add(btnAddNote, 1, 7);

        lblStatus = new Label("✅ Готов");

        root.setLeft(leftPanel);
        root.setCenter(formPanel);
        root.setBottom(lblStatus);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveToDatabase() {
        List<Client> allClients = service.getAllClients();
        storage.saveAll(allClients);
    }

    private boolean showLoginDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🔐 Вход в CRM");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblTitle = new Label("CRM MANAGER SYSTEM");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField tfUsername = new TextField();
        tfUsername.setPromptText("admin / user");
        PasswordField pfPassword = new PasswordField();
        pfPassword.setPromptText("пароль");

        grid.add(lblTitle, 0, 0, 2, 1);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(tfUsername, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(pfPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        while (true) {
            if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                if (authService.login(tfUsername.getText(), pfPassword.getText())) {
                    return true;
                } else {
                    showAlert("Ошибка", "❌ Неверный логин или пароль!\n\nДоступные:\nAdmin: admin / admin123\nUser: user / user123");
                }
            } else {
                return false;
            }
        }
    }

    private void showClientDetails(Client client) {
        if (client == null) return;

        lblId.setText(String.valueOf(client.getId()));
        tfName.setText(client.getName());
        tfEmail.setText(client.getEmail());
        tfPhone.setText(client.getPhone());
        tfCompany.setText(client.getCompany());

        StringBuilder sb = new StringBuilder();
        for (String note : client.getInteractions()) {
            sb.append("• ").append(note).append("\n");
        }
        taInteractions.setText(sb.toString());
    }

    private void addClient() {
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();
        String company = tfCompany.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Ошибка", "Имя и Email обязательны!");
            return;
        }

        if (!email.contains("@")) {
            showAlert("Ошибка", "Неверный формат email!");
            return;
        }

        service.addClient(name, email, phone, company);
        refreshList();
        saveToDatabase();
        lblStatus.setText("✅ Клиент добавлен: " + name);

        tfName.clear();
        tfEmail.clear();
        tfPhone.clear();
        tfCompany.clear();
        lblId.setText("");
        taInteractions.clear();
    }

    private void updateClient() {
        Client selected = lvClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента из списка!");
            return;
        }

        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();
        String company = tfCompany.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Ошибка", "Имя и Email обязательны!");
            return;
        }

        service.updateClient(selected.getId(), name, email, phone, company);
        refreshList();
        saveToDatabase();
        lblStatus.setText("✅ Клиент обновлён: " + name);
    }

    private void deleteClient() {
        Client selected = lvClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента из списка!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить клиента \"" + selected.getName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Подтверждение удаления");

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            service.deleteClient(selected.getId());
            refreshList();
            saveToDatabase();
            lblStatus.setText("🗑️ Клиент удалён: " + selected.getName());

            tfName.clear();
            tfEmail.clear();
            tfPhone.clear();
            tfCompany.clear();
            lblId.setText("");
            taInteractions.clear();
        }
    }

    private void addNote() {
        Client selected = lvClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента из списка!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("📝 Добавить заметку");
        dialog.setHeaderText("Клиент: " + selected.getName());
        dialog.setContentText("Текст заметки:");

        dialog.showAndWait().ifPresent(note -> {
            if (!note.trim().isEmpty()) {
                service.addInteraction(selected.getId(), note);
                refreshList();
                saveToDatabase();
                showClientDetails(selected);
                lblStatus.setText("📝 Заметка добавлена для: " + selected.getName());
            }
        });
    }

    private void exportToCsv() {
        TextInputDialog dialog = new TextInputDialog("clients_export");
        dialog.setTitle("📁 Экспорт в CSV");
        dialog.setHeaderText("Введите имя файла");
        dialog.setContentText("Имя файла:");

        dialog.showAndWait().ifPresent(filename -> {
            CsvExporter exporter = new CsvExporter();
            try {
                exporter.exportToCsv(service.getAllClients(), filename);
                lblStatus.setText("✅ Экспортировано в " + filename + ".csv");
                showAlert("Успех", "Экспортировано " + service.getClientCount() + " клиентов в файл " + filename + ".csv");
            } catch (IOException e) {
                showAlert("Ошибка", "Экспорт не удался: " + e.getMessage());
            }
        });
    }

    private void importFromCsv() {
        TextInputDialog dialog = new TextInputDialog("clients.csv");
        dialog.setTitle("📂 Импорт из CSV");
        dialog.setHeaderText("Введите имя CSV файла из папки data/");
        dialog.setContentText("Имя файла (например: clients.csv):");

        dialog.showAndWait().ifPresent(filename -> {
            String fullPath = "data/" + (filename.endsWith(".csv") ? filename : filename + ".csv");
            File file = new File(fullPath);

            if (!file.exists()) {
                showAlert("Ошибка", "Файл не найден: " + fullPath +
                        "\n\nУбедитесь, что файл существует в папке data/");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
                reader.readLine(); // пропускаем заголовки
                String line;
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
                refreshList();
                saveToDatabase();
                lblStatus.setText("✅ Импортировано " + count + " клиентов из " + filename);
                showAlert("Успех", "Импортировано " + count + " клиентов из файла " + filename);

            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось прочитать файл: " + e.getMessage());
            }
        });
    }

    private void refreshList() {
        clientList.clear();
        clientList.addAll(service.getAllClients());
        lvClients.refresh();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}