package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    private TextField emailField;
    private PasswordField passwordField;
    private Label statusLabel;
    private Main mainApp;

    public Login(Main mainApp) {
        this.mainApp = mainApp;
    }

    public VBox createLoginForm() {
        Label titleLabel = new Label("Go Go");
        Label titleLabel2 = new Label("Query");

        titleLabel.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white; -fx-translate-y: 80px;");
        titleLabel2.setStyle("-fx-font-size: 95px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: #e56e05; -fx-translate-x: 50px;");

        VBox titleContainer = new VBox(titleLabel, titleLabel2);
        titleContainer.setAlignment(Pos.BOTTOM_LEFT);
        titleContainer.setSpacing(-10);

        Label loginLabel = new Label("Login");
        Label emailLabel = new Label("Email");
        Label passwordLabel = new Label("Password");

        loginLabel.setStyle(
                "-fx-font-size: 25px; " +
                "-fx-font-weight: bolder; " +
                "-fx-text-fill: #000000;" +
                "-fx-border-color: #000000;" +
                "-fx-border-width: 0 0 2px 0;" +
                "-fx-border-insets: 0;" +
                "-fx-padding: 0 0 10px 0;"
        );
        loginLabel.setPrefWidth(180); // mengatur panjang garis hitam
        emailLabel.setStyle("-fx-text-fill: #000000;");
        passwordLabel.setStyle("-fx-text-fill: #000000;");

        emailField = new TextField();
        passwordField = new PasswordField();

        Button logInButton = new Button("Login");
        logInButton.setOnAction(event -> handleLogin());
        logInButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-background-color: #e56e05;");
        logInButton.setPrefWidth(400);

        statusLabel = new Label();

        Text staticText = new Text("Are you new? Register ");
        Hyperlink registerLink = new Hyperlink("Here!");
        registerLink.setOnAction(event -> mainApp.switchToRegisterForm());
        TextFlow registerTextFlow = new TextFlow(staticText, registerLink);

        GridPane loginGridPane = new GridPane();
        loginGridPane.setAlignment(Pos.CENTER);
        loginGridPane.setVgap(10);
        loginGridPane.setHgap(10);
        loginGridPane.setPadding(new Insets(20));
        loginGridPane.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10px; -fx-border-width: 2px; -fx-border-radius: 10px;");

        // agar form login bisa menyesuaikan bg-form dgn baik
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(100);
        loginGridPane.getColumnConstraints().add(column1);

        loginGridPane.add(loginLabel, 0, 0);
        loginGridPane.add(emailLabel, 0, 1);
        loginGridPane.add(emailField, 0, 2);
        loginGridPane.add(passwordLabel, 0, 3);
        loginGridPane.add(passwordField, 0, 4);
        loginGridPane.add(logInButton, 0, 5);
        loginGridPane.add(registerTextFlow, 0, 6);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));
        vBox.getChildren().addAll(titleContainer, loginGridPane, statusLabel);
        vBox.setMaxWidth(350);

        return vBox;
    }

    private String checkCredentials(String email, String password) {
        String query = "SELECT UserRole FROM MsUser WHERE UserEmail = ? AND UserPassword = ?";
        try (Connection connection = DriverManager.getConnection(DatabaseSchema.URL, DatabaseSchema.USERNAME, DatabaseSchema.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getString("UserRole");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // return null klo gagal login
    }
    
    private int getUserId(String email, String password) {
        String query = "SELECT UserID FROM MsUser WHERE UserEmail = ? AND UserPassword = ?";
        try (Connection connection = DriverManager.getConnection(DatabaseSchema.URL, DatabaseSchema.USERNAME, DatabaseSchema.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Login", "Log in Failed.", "Email or Password cannot be empty.");
            return;
        }
        
        String userRole = checkCredentials(email, password);
        int userId = getUserId(email, password);

        if (userRole != null && userId != -1) {
            statusLabel.setText("Login successful!");
            mainApp.setUserEmail(email);
            mainApp.setUserRole(userRole);
            mainApp.setCurrentUserId(userId); // Simpan userId setelah login
            
            // Arahkan berdasarkan role
            if ("Manager".equalsIgnoreCase(userRole)) {
                mainApp.switchToManagerDashboard();
            } else if ("Shopper".equalsIgnoreCase(userRole)) {
                mainApp.switchToShopperDashboard();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid credentials.", "The provided email or password is incorrect.");
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
