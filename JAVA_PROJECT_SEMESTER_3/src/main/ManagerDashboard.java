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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import page.ManagerTransactionStore;
public class ManagerDashboard extends Application {
	private static final ObservableList<ManagerTransactionStore> oTasks = FXCollections.observableArrayList();
    private Stage primaryStage;
    private Main mainApp; // Mengasumsikan ada kelas Main untuk navigasi, bisa diabaikan jika tidak diperlukan

    public ManagerDashboard(Main mainApp) {
        this.mainApp = mainApp;
    }

    public ManagerDashboard() {
        // Default constructor
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

    public VBox createManagerDashboardPage(Stage primaryStage) {
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
        logOut.setOnAction(e -> navigateToLoginPage());

        // Tambahkan menu item ke dalam menu, lalu tambahkan menu ke dalam menu bar
        managerMenu.getItems().addAll(addItem, manageQueue, logOut);
        menuBar.getMenus().add(managerMenu);

        // Layout utama
        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.getChildren().add(menuBar);

        return mainLayout;
    }

	private void navigateToQueueManagementPage() {
    // Ambil transaksi menggunakan method dari DatabaseSchema
    List<ManagerTransactionStore> transactions = DatabaseSchema.getTransactionDetails(DatabaseSchema.getAllUserIds());

    // Membuka halaman Manage Queue
    Stage queueManagementStage = new Stage();
    queueManagementStage.setTitle("Manage Queue");

    // Layout untuk halaman Manage Queue
    VBox queueManagementLayout = new VBox(10);
    queueManagementLayout.setPadding(new Insets(10));
    queueManagementLayout.setStyle("-fx-background-color: #007BFF;"); // Set warna biru untuk background Manage Queue

    // Label halaman
    Label queueLabel = new Label("Queue Manager");
    queueLabel.setStyle("-fx-text-fill: #394867; -fx-font-size: 25px; "
            + "-fx-padding: 4px;"); // Warna biru dove gelap untuk title
    queueLabel.setMaxWidth(Double.MAX_VALUE); // Supaya memenuhi lebar layout
    queueLabel.setAlignment(Pos.CENTER_LEFT);

    // Tabel untuk menampilkan antrian
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

    TableColumn<ManagerTransactionStore, String> statusColumn = new TableColumn<>("Transaction Status");
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    queueTable.getColumns().addAll(idTransColumn, idCustColumn, emailCustColumn, dateCustColumn, totalColumn, statusColumn);

    // Clear previous data
    oTasks.clear();

    // Tambahkan transaksi ke ObservableList
    oTasks.addAll(transactions);

    // Refresh tabel untuk memastikan data tampil
    queueTable.refresh();

    // Buttons untuk memanipulasi queue
    Button sendBtn = new Button("Send");
    sendBtn.setOnAction(e -> {
        ManagerTransactionStore selectedItem = queueTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedItem.setStatus("Sent");
            queueTable.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Queue item sent.");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to send.");
        }
    });

    queueManagementLayout.getChildren().addAll(queueLabel, queueTable, sendBtn);

    // Scene untuk queue management
    Scene scene = new Scene(queueManagementLayout, 750, 500);
    scene.setFill(javafx.scene.paint.Color.web("#394867")); // Set warna biru dove untuk background scene
    queueManagementStage.setScene(scene);
    queueManagementStage.show();
}

    private void navigateToLoginPage() {
        // Logika navigasi ke halaman Login
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Logging Out...");
    }
    
    private void openAddItemForm() {
        // Membuat pop-up window untuk Add Item
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Add Item");

        // GridPane untuk tata letak
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Label dan input field
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

        // Tombol Add Item
        Button addItemButton = new Button("Add Item");
        addItemButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // Event handler untuk Add Item
        addItemButton.setOnAction(e -> {
            String itemName = itemNameField.getText();
            String itemDesc = itemDescArea.getText();
            String itemCategory = itemCategoryField.getText();
            int quantity = quantitySpinner.getValue();

            // Parsing harga menjadi double
            double itemPrice = 0;
            try {
                itemPrice = Double.parseDouble(itemPriceField.getText());  // Coba mengonversi harga
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Item Price must be a valid number.");
                return;  // Hentikan proses jika format harga salah
            }

            // Validasi input
            String validationMessage = validateInputs(itemName, itemPrice, itemDesc, itemCategory, quantity);
            if (validationMessage != null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", validationMessage);
            } else {
                // Jika validasi lolos
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully!");
                // Menambahkan item ke database
                DatabaseSchema.addItem(itemName, itemPrice, itemDesc, itemCategory, quantity);
                popUpWindow.close();
            }
        });

        // Menambahkan komponen ke GridPane
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
        
        BorderPane bp = new BorderPane();
        bp.setCenter(gridPane);

        // Membuat dan menampilkan pop-up scene
        Scene scene = new Scene(bp, 600, 400);
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();
    }
    
    private String validateInputs(String itemName, Double itemPrice, String itemDesc, String itemCategory, int quantity) {
        // Validasi Item Name
        if (itemName == null || itemName.trim().isEmpty() || itemName.length() < 5 || itemName.length() > 70) {
            return "Item Name must be between 5 and 70 characters.";
        }

        // Validasi Item Description
        if (itemDesc == null || itemDesc.trim().isEmpty() || itemDesc.length() < 10 || itemDesc.length() > 255) {
            return "Item Description must be between 10 and 255 characters.";
        }

        // Validasi Item Category
        if (itemCategory == null || itemCategory.trim().isEmpty()) {
            return "Item Category must not be empty.";
        }

        // Validasi Item Price
        try {
            double price = itemPrice;
            if (price < 0.50 || price > 900000) {
                return "Item Price must be between $0.50 and $900,000.";
            }
        } catch (NumberFormatException e) {
            return "Item Price must be a valid number.";
        }

        // Validasi Quantity
        if (quantity < 1 || quantity > 300) {
            return "Quantity must be a positive integer between 1 and 300.";
        }

        return null; // Semua validasi lolos
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
