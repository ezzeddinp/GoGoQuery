package main;

import java.time.LocalDate;

import javafx.geometry.Insets;  
import javafx.geometry.Pos;  
import javafx.scene.control.*;  
import javafx.scene.layout.HBox;  
import javafx.scene.layout.VBox;  
import javafx.scene.paint.Color;  
import javafx.scene.shape.Circle;  
import javafx.stage.Stage;  

public class RoleSelection {  
    private Main mainApp;  

    public RoleSelection(Main mainApp) {  
        this.mainApp = mainApp;  
    }  

    // Page role utama
    public VBox createRoleSelectionForm(Stage primaryStage) {  
        Label titleLabel = new Label("Go Go");
        Label titleLabel2 = new Label("Query");

        titleLabel.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white; -fx-translate-y: 80px;");
        titleLabel2.setStyle("-fx-font-size: 95px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: #e56e05; -fx-translate-x: 50px;");

        
        // membuat container untuk titleLabel2 agar mudah memainkan posisi
        HBox titleContainer = new HBox(titleLabel2);
        titleContainer.setAlignment(Pos.CENTER); // Aligns titleLabel2 ke kiri
        titleContainer.setSpacing(0);


        // menggunakan createRoleBox sehingga lebih dinamis dibanding membuat 2 class untuk manager & shopper
        VBox managerBox = createRoleBox("Manager", "Manage products and deliveries, be the ruler!", "Register as Manager");  
        VBox shopperBox = createRoleBox("Shopper", "Search products, manage your cart, go shopping!", "Register as Shopper");  

        // layout box antara manager dan shopper
        HBox hBox = new HBox();  
        hBox.setAlignment(Pos.TOP_CENTER);  
        hBox.setSpacing(20);  
        hBox.setPadding(new Insets(20));  
        hBox.getChildren().addAll(managerBox, shopperBox);   

        // layout tulisan gogoquery dgn 2 box
        VBox mainBox = new VBox();  
        mainBox.setAlignment(Pos.TOP_CENTER);  
        mainBox.setSpacing(10);  
        mainBox.getChildren().addAll(titleLabel, titleContainer, hBox);  
        mainBox.setMaxWidth(975); // padding width diluar kedua form  

        return mainBox;  
    }  

    // design UI  Role didalam form sprti profile, label, description, button
    private VBox createRoleBox(String role, String description, String buttonText) {  
        
        Circle circle = new Circle(35, Color.GREY); // Profile 

        Label roleLabel = new Label(role);  
        roleLabel.setStyle(
        		"-fx-font-size: 18px;"
        		+ "-fx-font-weight: bold;"
        		+
                "-fx-border-color: #000000;" +
                "-fx-border-width: 0 0 2px 0;" +                        
                "-fx-border-insets: 0;" +
                "-fx-padding: 0 0 10px 0;" +
                "-fx-text-align: center;"
        );
        roleLabel.setPrefWidth(270);
        roleLabel.setAlignment(Pos.CENTER);

        Label descriptionLabel = new Label(description);   // description shopper n manager
        descriptionLabel.setWrapText(true);  

        Button registerButton = new Button(buttonText);  // button untuk register as manager/shopper
        registerButton.setOnAction(event -> handleRoleSelection(role)); 
        registerButton.setStyle(
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-background-color: #e56e05;"
        );
        registerButton.setPrefWidth(270);
        
        

        VBox vBox = new VBox();  
        vBox.setAlignment(Pos.CENTER);  
        vBox.setSpacing(10); // gap/spasi label, description, button, profile
        vBox.getChildren().addAll(circle, roleLabel, descriptionLabel, registerButton); // Container form
        vBox.setPadding(new Insets(35, 50, 35, 50)); // padding yg ada didalam form role
        vBox.setPrefWidth(950); // pake Pref biar dia bisa fleksibel/menyesuaikan   
        vBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 2; -fx-background-radius: 4.5;"); // form background

        return vBox;  
    }  

    private void handleRoleSelection(String role) {  
        String email = mainApp.getUserEmail();
        String password = mainApp.getUserPassword();
        LocalDate dob = mainApp.getUserDob();
        boolean isMale = mainApp.getIsUserMale();
        boolean isFemale = mainApp.getIsUserFemale();

        showAlert(Alert.AlertType.INFORMATION, "Role Selected", "You have selected: " + role);
        
        // variable yg ngehandle add user ke db
        boolean isRegistered = DatabaseSchema.insertUser(dob, email, password, isMale, isFemale, role);
        
        if (isRegistered) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Information", "Please log in with your newly created account.");
            // logika ketika user memilih salah satu maka akan diperlukan untuk re-log sesuai case.
            if (role.equals("Manager")) {
				mainApp.switchToLoginForm(); 
			} else if(role.equals("Shopper")) {
				mainApp.switchToLoginForm();
			}
            
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Registration failed. Please try again.");
        }
    }  

    private void showAlert(Alert.AlertType alertType, String title, String message) {  
        Alert alert = new Alert(alertType);  
        alert.setTitle(title);  
        alert.setContentText(message);  
        alert.showAndWait();  
    }  
}