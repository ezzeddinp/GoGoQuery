package main;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// File ini lebih mengarah ke bgmn logika perpindahan page ke page
public class Main extends Application {
	private static int currentUserId; // Menyimpan user ID yang login saat ini
    private String userEmail;
    private String userPassword;
    private LocalDate userDob;
    private boolean isUserMale;
    private boolean isUserFemale;
    private String userRole;
	
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("My Application");

        switchToLoginForm(); // halaman default/awal Login
    }

    // logika ke login dari register
    public void switchToLoginForm() {
        Login login = new Login(this); // import kelas Login
        BorderPane loginPane = new BorderPane();
        
        loginPane.setCenter(login.createLoginForm());
        loginPane.setStyle("-fx-background-color: #02000a;");
        Scene scene = new Scene(loginPane, 800, 700); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // logika ke register form dari login form
    public void switchToRegisterForm() {
        Register register = new Register(this);
        BorderPane registerPane = new BorderPane();
        
        registerPane.setCenter(register.createRegisterForm(primaryStage));
        registerPane.setStyle("-fx-background-color: #02000a;");
        Scene scene = new Scene(registerPane, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // logika ke selection form dari register/login form
    public void switchToRoleSelectionForm() {
        RoleSelection roleSelection = new RoleSelection(this); // import kelas Role
        BorderPane roleSelectionPane = new BorderPane();
        
        roleSelectionPane.setCenter(roleSelection.createRoleSelectionForm(primaryStage));
        roleSelectionPane.setStyle("-fx-background-color: #02000a;");
        Scene scene = new Scene(roleSelectionPane, 800, 700); // ukuran scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void switchToShopperDashboard() {
        if (currentUserId == 0) {
            ShopperDashboard.showAlert(AlertType.ERROR, "Access Denied", "Please log in first!");
            return;
        }
        ShopperDashboard shopperDashboard = new ShopperDashboard(this);
        BorderPane shopperDashboardPane = new BorderPane();

        shopperDashboardPane.setCenter(shopperDashboard.createShopperDashboardPage(primaryStage));
        shopperDashboardPane.setStyle("-fx-background-color: #02000a;");
        Scene scene = new Scene(shopperDashboardPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchToManagerDashboard() {
        if (currentUserId == 0) {
            ShopperDashboard.showAlert(AlertType.ERROR, "Access Denied", "Please log in first!");
            return;
        }
        ManagerDashboard managerDashboard = new ManagerDashboard(this);
        BorderPane managerDashboardPane = new BorderPane();

        managerDashboardPane.setCenter(managerDashboard.createManagerDashboardPage(primaryStage));
        managerDashboardPane.setStyle("-fx-background-color: #ffffff;");
        Scene scene = new Scene(managerDashboardPane, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // dibawah ini method sbg penyimpanan sementara ketika user telah input data 
    // di register page lalu menuju ke selection role stelah itu baru terSave
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserDob(LocalDate userDob) {
        this.userDob = userDob;
    }

    public void setUserGender(boolean isMale, boolean isFemale) {
        this.isUserMale = isMale;
        this.isUserFemale = isFemale;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public LocalDate getUserDob() {
        return userDob;
    }

    public boolean getIsUserMale() {
        return isUserMale;
    }

    public boolean getIsUserFemale() {
        return isUserFemale;
    }

    public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public static void main(String[] args) {
        launch(args);
    }
}
