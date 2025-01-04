package main;

import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.layout.StackPane;  // Import StackPane
import javafx.stage.Stage;

public class RoleSelection {
    private Main mainApp;

    public RoleSelection(Main mainApp) {
        this.mainApp = mainApp;
    }

    // Halaman utama role selection
    public VBox createRoleSelectionForm(Stage primaryStage) {
        // Judul utama
        Label titleLabel = new Label("Go Go");
        Label titleLabel2 = new Label("Query");

        titleLabel.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white; -fx-translate-y: 80px;");
        titleLabel2.setStyle("-fx-font-size: 95px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: #e56e05; -fx-translate-x: 50px;");

        // Container untuk titleLabel2 supaya lebih gampang diatur posisinya
        HBox titleContainer = new HBox(titleLabel2);
        titleContainer.setAlignment(Pos.CENTER); // Biar titleLabel2 align ke tengah
        titleContainer.setSpacing(0);

        // Membuat dua form role menggunakan createRoleBox, biar lebih dinamis
        VBox managerBox = createRoleBox("Manager", "Manage products and deliveries, be the ruler!", "Register as Manager", "m", Color.DARKSLATEBLUE);
        VBox shopperBox = createRoleBox("Shopper", "Search products, manage your cart, go shopping!", "Register as Shopper", "s", Color.PURPLE);

        // Layout untuk box manager dan shopper
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(20));
        hBox.getChildren().addAll(managerBox, shopperBox);

        // Gabungkan judul dan box ke dalam main layout
        VBox mainBox = new VBox();
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setSpacing(10);
        mainBox.getChildren().addAll(titleLabel, titleContainer, hBox);
        mainBox.setMaxWidth(975); // padding width di luar kedua form  

        return mainBox;
    }

    // Desain UI untuk tiap role (profile, label, description, button)
    private VBox createRoleBox(String role, String description, String buttonText, String profileLetter, Color profileColor) {
        
        // Membuat lingkaran untuk profil dengan warna yang ditentukan
        Circle circle = new Circle(35, profileColor);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(3);

        // Membuat lingkaran kecil di dalam profil untuk status (misalnya hijau)
        Circle statusCircle = new Circle(8, Color.LIMEGREEN);
        statusCircle.setTranslateX(20);  // Posisikan status circle di sisi kanan lingkaran profil
        statusCircle.setTranslateY(25);  // Posisikan sedikit di atas lingkaran profil

        // Menambahkan label untuk huruf profil (misalnya "m" untuk Manager, "s" untuk Shopper)
        Label profileLabel = new Label(profileLetter);
        profileLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        profileLabel.setFont(Font.font(20));

        // Menggunakan StackPane untuk menumpuk elemen-elemen (lingkaran, huruf, dan status)
        StackPane profileStack = new StackPane();
        profileStack.getChildren().addAll(circle, profileLabel, statusCircle);

        // Label untuk role dan deskripsi
        Label roleLabel = new Label(role);
        roleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; "
                + "-fx-border-color: #000000;" +
                "-fx-border-width: 0 0 2px 0;" +
                "-fx-border-insets: 0;" +
                "-fx-padding: 0 0 10px 0;" +
                "-fx-text-align: center;");
        roleLabel.setPrefWidth(270);
        roleLabel.setAlignment(Pos.CENTER);

        // Deskripsi untuk role (Manager atau Shopper)
        Label descriptionLabel = new Label(description);  
        descriptionLabel.setWrapText(true);

        // Tombol untuk register sebagai Manager/Shopper
        Button registerButton = new Button(buttonText);
        registerButton.setOnAction(event -> handleRoleSelection(role));
        registerButton.setStyle("-fx-font-size: 12px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #FFFFFF; "
                + "-fx-background-color: #e56e05;");
        registerButton.setPrefWidth(270);

        // Membuat form untuk tiap role (Manager/Shopper)
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);  // Spasi antara label, description, button, profile
        vBox.getChildren().addAll(profileStack, roleLabel, descriptionLabel, registerButton);
        vBox.setPadding(new Insets(35, 50, 35, 50)); // Padding di dalam form role
        vBox.setPrefWidth(950); // Biar form bisa menyesuaikan ukuran
        vBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 2; -fx-background-radius: 4.5;"); // Background form

        return vBox;
    }

    // Menangani aksi saat memilih role (Manager/Shopper)
    private void handleRoleSelection(String role) {
        // Ambil data user yang sudah dimasukkan
        String email = mainApp.getUserEmail();
        String password = mainApp.getUserPassword();
        LocalDate dob = mainApp.getUserDob();
        boolean isMale = mainApp.getIsUserMale();
        boolean isFemale = mainApp.getIsUserFemale();

        // Menampilkan pesan bahwa role sudah dipilih
        showAlert(Alert.AlertType.INFORMATION, "Role Selected", "You have selected: " + role);
        
        // Cek apakah user berhasil terdaftar
        boolean isRegistered = DatabaseSchema.insertUser(dob, email, password, isMale, isFemale, role);
        
        // jika registered maka akan muncul notif dan user diminta untuk login
        if (isRegistered) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Information", "Please log in with your newly created account.");
            // Arahkan user ke halaman login setelah pendaftaran sukses
            if (role.equals("Manager")) {
                mainApp.switchToLoginForm(); 
            } else if(role.equals("Shopper")) {
                mainApp.switchToLoginForm();
            }
            
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Registration failed. Please try again.");
        }
    }

    // Menampilkan pesan alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
