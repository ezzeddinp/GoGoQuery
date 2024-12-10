package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.time.LocalDate;

public class Register {
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private DatePicker datePicker;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private CheckBox termsCheckBox;
    private Main mainApp;

    public Register(Main mainApp) {
        this.mainApp = mainApp;
    }

    public VBox createRegisterForm(Stage primaryStage) {
    	// GOGO QUERY TITLE
        Label titleLabel = new Label("Go Go");
        Label titleLabel2 = new Label("Query");

        titleLabel.setStyle("-fx-font-size: 70px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white; -fx-translate-y: 65px; -fx-translate-x: 25px;");
        titleLabel2.setStyle("-fx-font-size: 90px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: #e56e05; -fx-translate-x: 50px; -fx-padding: 0 0 -10px 0");

        VBox titleContainer = new VBox(titleLabel, titleLabel2);
        titleContainer.setAlignment(Pos.BOTTOM_LEFT);
        titleContainer.setSpacing(0);
        titleContainer.setPadding(new Insets(-40, 0, 0, 0));

    	
        Label registerLabel = new Label("Register");
        Label emailLabel = new Label("Email");
        Label passwordLabel = new Label("Password");
        Label confirmPasswordLabel = new Label("Confirm Password");
        Label dobLabel = new Label("Date of Birth");
        Label genderLabel = new Label("Gender");

        emailField = new TextField();
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        datePicker = new DatePicker();

        ToggleGroup genderGroup = new ToggleGroup();
        maleRadioButton = new RadioButton("Male");
        femaleRadioButton = new RadioButton("Female");
        maleRadioButton.setToggleGroup(genderGroup);
        femaleRadioButton.setToggleGroup(genderGroup);

        // Grouping male n female
        HBox genderBox = new HBox(10); // gap 10px antara male dan female button
        genderBox.getChildren().addAll(maleRadioButton, femaleRadioButton);

        // Terms and Conditions TextFlow
        Text termsStaticText = new Text("I accept the ");
        Text termsText = new Text("Terms and Conditions");
        termsText.setFill(Color.BLUE); 
        termsText.setUnderline(true); 

        TextFlow termsTextFlow = new TextFlow(termsStaticText, termsText);
        termsCheckBox = new CheckBox();

        // ngebuat textflow dan checkbox sejajar
        GridPane termsGrid = new GridPane();
        termsGrid.setHgap(5);
        termsGrid.add(termsCheckBox, 0, 0);
        termsGrid.add(termsTextFlow, 1, 0);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(event -> handleRegistration());
        registerButton.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-background-color: #e56e05;"
        );
        registerButton.setPrefWidth(300);

        Text staticText = new Text("Already have an Account? Sign in ");

        Hyperlink loginLink = new Hyperlink("Here!");
        loginLink.setOnAction(event -> mainApp.switchToLoginForm());

        TextFlow loginTextFlow = new TextFlow(staticText, loginLink);

        // Style register label
        registerLabel.setStyle(
            "-fx-font-size: 25px; " +
            "-fx-font-weight: bolder; " +
            "-fx-text-fill: #000000;" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 0 0 2px 0;" +                        
            "-fx-border-insets: 0;" +
            "-fx-padding: 0 0 10px 0;"
        );
        registerLabel.setPrefWidth(180);

        GridPane registerGridPane = new GridPane();
        registerGridPane.setAlignment(Pos.TOP_CENTER);
        registerGridPane.setVgap(10);
        registerGridPane.setHgap(10);
        registerGridPane.setPadding(new Insets(20));
        registerGridPane.setMaxHeight(500);
        registerGridPane.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px;" +
            "-fx-"
        );
        registerGridPane.setMaxWidth(300);

        registerGridPane.add(registerLabel, 0, 0);
        registerGridPane.add(emailLabel, 0, 1);
        registerGridPane.add(emailField, 0, 2, 2, 1);
        registerGridPane.add(passwordLabel, 0, 3);
        registerGridPane.add(passwordField, 0, 4, 2, 1);
        registerGridPane.add(confirmPasswordLabel, 0, 5);
        registerGridPane.add(confirmPasswordField, 0, 6, 2, 1);
        registerGridPane.add(dobLabel, 0, 7);
        registerGridPane.add(datePicker, 0, 8, 2, 1);
        registerGridPane.add(genderLabel, 0, 9);
        registerGridPane.add(genderBox, 0, 10, 2, 1); // Add HBox with radio buttons here
        registerGridPane.add(termsGrid, 0, 11, 2, 1);
        registerGridPane.add(registerButton, 0, 12, 2, 1);
        registerGridPane.add(loginTextFlow, 0, 13, 2, 1);

        // layout title GOGO QUERY dan FORM register
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));
        registerGridPane.setPrefHeight(800);
        vBox.getChildren().addAll(titleContainer,registerGridPane); // menggabungkan gogo query title dengan form shingga sprti flex-col
        vBox.setMaxWidth(400);

        return vBox;
    }

    private void handleRegistration() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        LocalDate dob = datePicker.getValue();
        boolean isMale = maleRadioButton.isSelected();
        boolean isFemale = femaleRadioButton.isSelected();
        boolean termsAccepted = termsCheckBox.isSelected();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob == null ||
                (!isMale && !isFemale) || !termsAccepted) {
            showAlert(Alert.AlertType.ERROR, "Incomplete Form", "Please fill out all fields.");
            return;
        }

        if (!email.endsWith("@gomail.com")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Email must end with '@gomail.com'.");
            return;
        }

        if (!email.matches("[a-zA-Z0-9._]+@gomail.com")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Email contains invalid characters.");
            return;
        }
        // halo
        if (!DatabaseSchema.isEmailUnique(email)) {
            showAlert(Alert.AlertType.ERROR, "Email Taken", "Email is already taken.");
            return;
        }

        if (!password.matches("[a-zA-Z0-9]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Password", "Password must be alphanumeric.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords Mismatch", "Passwords do not match.");
            return;
        }

        if (dob.isAfter(LocalDate.now().minusYears(17))) { 
            showAlert(Alert.AlertType.ERROR, "Age Restriction", "User must be older than 17 years.");
            return;
        }
        
        // save sementara
        mainApp.setUserEmail(email);
        mainApp.setUserPassword(password);
        mainApp.setUserDob(dob);
        mainApp.setUserGender(isMale, isFemale);
        
        // redirect ke selection role stlah input sudah benar semua
        mainApp.switchToRoleSelectionForm();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
