package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import page.ManagerTransactionStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ManagerDashboard extends Application {
    private static final ObservableList<ManagerTransactionStore> oTasks = FXCollections.observableArrayList();
    private Stage primaryStage;
    private Stage queueManagementStage;
    private final Main mainApp;

    public ManagerDashboard(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox managerDashboardPage = createManagerDashboardPage(primaryStage);
        Scene managerScene = new Scene(managerDashboardPage, 800, 600);
        primaryStage.setScene(managerScene);
        primaryStage.setTitle("Manager Dashboard");
        primaryStage.show();
    }

    VBox createManagerDashboardPage(Stage primaryStage2) {
        // Menu Bar dan Menu
        MenuBar menuBar = new MenuBar();
        Menu managerMenu = new Menu("Menu");

        // Menu Items
        MenuItem addItem = new MenuItem("Add Item");
        MenuItem manageQueue = new MenuItem("Manage Queue");
        MenuItem logOut = new MenuItem("Log Out");

        // Tambahkan aksi pada setiap menu item
        addItem.setOnAction(e -> openAddItemForm());
        manageQueue.setOnAction(e -> navigateToQueueManagementPage());
        logOut.setOnAction(e -> mainApp.switchToLoginForm());

        managerMenu.getItems().addAll(addItem, manageQueue, logOut);
        menuBar.getMenus().add(managerMenu);

        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.getChildren().add(menuBar);

        return mainLayout;
    }

    private void navigateToQueueManagementPage() {
        if (queueManagementStage != null && queueManagementStage.isShowing()) {
            return;  // Don't open the window if it's already showing
        }

        List<ManagerTransactionStore> transactions = DatabaseSchema.getTransactionDetails(DatabaseSchema.getAllUserIds());
        if (transactions == null || transactions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Data", "No Transactions", "No transactions available.");
            return;
        }

        Window queueManagementWindow = new Window();
        queueManagementWindow.setPrefSize(900, 500);

        VBox queueManagementLayout = new VBox(10);
        queueManagementLayout.setPadding(new Insets(10));

        Label queueLabel = new Label("Queue Manager");
        queueLabel.setStyle("-fx-text-fill: #394867; -fx-font-size: 25px;");
        queueLabel.setMaxWidth(Double.MAX_VALUE);
        queueLabel.setAlignment(Pos.CENTER_LEFT);

        TableView<ManagerTransactionStore> queueTable = new TableView<>(oTasks);

        TableColumn<ManagerTransactionStore, Integer> idTransColumn = new TableColumn<>("Transaction ID");
        idTransColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        TableColumn<ManagerTransactionStore, Integer> idCustColumn = new TableColumn<>("Customer ID");
        idCustColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<ManagerTransactionStore, String> emailCustColumn = new TableColumn<>("Customer Email");
        emailCustColumn.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));

        TableColumn<ManagerTransactionStore, Date> dateCustColumn = new TableColumn<>("Date");
        dateCustColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCustColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(new SimpleDateFormat("dd-MM-yyyy").format(date));
                }
            }
        });

        TableColumn<ManagerTransactionStore, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<ManagerTransactionStore, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        queueTable.getColumns().addAll(idTransColumn, idCustColumn, emailCustColumn, dateCustColumn, totalColumn, statusColumn);
        oTasks.clear();
        oTasks.addAll(transactions);
        queueTable.refresh();

        Button sendBtn = new Button("Send Package");
        sendBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        sendBtn.setOnAction(e -> {
            ManagerTransactionStore selectedItem = queueTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int transactionId = selectedItem.getTransactionId();
                boolean isUpdated = DatabaseSchema.updateSendPackageStatus(transactionId);
                if (isUpdated) {
                    selectedItem.setStatus("Sent");
                    queueTable.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Package Sent", "Queue item sent and status updated.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", "Failed to update status in the database.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Reference Error", "No Transaction Selected", "Please select a transaction.");
            }
        });

        queueManagementLayout.getChildren().addAll(queueLabel, queueTable, sendBtn);
        queueManagementWindow.getContentPane().getChildren().add(queueManagementLayout);

        queueManagementStage = new Stage();
        queueManagementStage.setScene(new Scene(queueManagementWindow));
        queueManagementStage.show();
    }

    private void openAddItemForm() {
        Window popUpWindow = new Window();
        popUpWindow.setPrefSize(900, 500);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label itemNameLabel = new Label("Item Name:");
        TextField itemNameField = new TextField();

        Label itemDescLabel = new Label("Item Description:");
        TextArea itemDescArea = new TextArea();
        itemDescArea.setPrefHeight(100);

        Label itemCategoryLabel = new Label("Item Category:");
        TextField itemCategoryField = new TextField();

        Label itemPriceLabel = new Label("Item Price:");
        TextField itemPriceField = new TextField();

        Label quantityLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 300, 1);
        quantitySpinner.setEditable(true);

        Button addItemButton = new Button("Add Item");
        addItemButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        addItemButton.setOnAction(e -> {
            String itemName = itemNameField.getText();
            String itemDesc = itemDescArea.getText();
            String itemCategory = itemCategoryField.getText();
            String itemPriceText = itemPriceField.getText();
            int quantity = quantitySpinner.getValue();

            // Validasi input
            String validationMessage = validateItemInputs(itemName, itemDesc, itemCategory, itemPriceText, quantity);
            if (validationMessage != null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Insert Error!", validationMessage);
            } else {
                try {
                    double itemPrice = Double.parseDouble(itemPriceText);

                    // insert data ke database
                    boolean isAdded = DatabaseSchema.addItem(itemName, itemPrice, itemDesc, itemCategory, quantity);
                    if (isAdded) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Item Added", "Item added successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Item Addition Failed", "Failed to add item.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Price", "Price must be a valid number.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Error Occurred", "An error occurred: " + ex.getMessage());
                }
            }
        });

        gridPane.add(itemNameLabel, 0, 0);
        gridPane.add(itemNameField, 1, 0);
        gridPane.add(itemDescLabel, 0, 1);
        gridPane.add(itemDescArea, 1, 1);
        gridPane.add(itemCategoryLabel, 0, 2);
        gridPane.add(itemCategoryField, 1, 2);
        gridPane.add(itemPriceLabel, 0, 3);
        gridPane.add(itemPriceField, 1, 3);
        gridPane.add(quantityLabel, 0, 4);
        gridPane.add(quantitySpinner, 1, 4);
        gridPane.add(addItemButton, 1, 5);

        gridPane.setAlignment(Pos.CENTER);

        popUpWindow.getContentPane().getChildren().add(gridPane);

        Stage popUpStage = new Stage();
        popUpStage.setScene(new Scene(popUpWindow));
        popUpStage.show();
    }
    
    // Method untuk validasi input manager ketika ingin menambahkan item
    private String validateItemInputs(String itemName, String itemDesc, String itemCategory, String itemPriceText, int quantity) {
        if (itemName.isEmpty() || itemDesc.isEmpty() || itemCategory.isEmpty() || itemPriceText.isEmpty()) {
            return "All fields must be filled.";
        }
        try {
            double itemPrice = Double.parseDouble(itemPriceText);
            if (itemPrice < 0.5 || itemPrice > 900000) {
                return "Item price must be between $0.50 and $900,000.";
            }
        } catch (NumberFormatException e) {
            return "Item price must be a valid number.";
        }
        if (itemName.length() < 5 || itemName.length() > 70) {
            return "Item name must be between 5 and 70 characters.";
        }
        if (itemDesc.length() < 10 || itemDesc.length() > 255) {
            return "Item description must be between 10 and 255 characters.";
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String contentMessage) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contentMessage);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
