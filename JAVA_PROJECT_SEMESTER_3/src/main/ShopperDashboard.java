package main;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.DatabaseSchema.Item;
import page.DetailCardPage;
import page.MyCartPage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopperDashboard {

    private Stage primaryStage;
    private Main mainApp; // Assuming there's a Main class for switching pages

 // Daftar produk dan kategori contoh
    private static List<Item> allProducts = fetchProductsFromDatabase();
    private static ListView<Item> productListView;
   
    
    private Label productListLabel;

 // Ambil kategori dari database
    private static List<String> categories = fetchCategoriesFromDatabase();

    public ShopperDashboard(Main mainApp) {
        this.mainApp = mainApp;
    }

    public VBox createShopperDashboardPage(Stage primaryStage) {
        this.primaryStage = primaryStage; // Simpan stage utama

        // Bikin pesan sambutan
        String userEmail = mainApp.getUserEmail();
        String username = extractNameFromEmail(userEmail);
        Label titleLabel = new Label("Welcome, ");
        Label titleLabel2 = new Label(username);

        // Set gaya untuk pesan sambutan
        titleLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: normal; -fx-font-style: italic; -fx-text-fill: white; -fx-translate-x: -50px; -fx-translate-y: 6px");
        titleLabel2.setStyle("-fx-font-size: 40px; -fx-font-weight: normal; -fx-font-style: italic; -fx-text-fill: orange; -fx-translate-x: -46px; -fx-translate-y: 6px");

        HBox titleContainer = new HBox(titleLabel, titleLabel2);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setStyle("-fx-translate-x: -127px;");
        titleContainer.setPadding(new Insets(20, 0, 0, 0));

        // Logo
        Label logo1 = new Label("Go Go");
        Label logo2 = new Label("Query");

        logo1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white;");
        logo2.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: orange; -fx-translate-y: -10px;");

        VBox logoContainer = new VBox(logo1, logo2);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setSpacing(-10);

        // Kolom pencarian
        TextField searchField = new TextField();
        searchField.setPromptText("Search items in GoGoQuery Store");
        searchField.setStyle("-fx-background-color: #444c5e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        searchField.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.setPadding(new Insets(3, 10, 2, 10));
        searchButton.setOnAction(event -> searchProducts(searchField.getText()));
        searchButton.setStyle("-fx-translate-x: -55; -fx-text-fill: white; -fx-border-color: transparent; -fx-background-color: #5f6f7f; -fx-font-weight: bold;");

        // Tombol keranjang
        Button myCartButton = new Button("My Cart");
        myCartButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
        myCartButton.setOnAction(event -> {
            // Redirect to MyCartPage ketika diklik
        	int userId = Main.getCurrentUserId();
            try {
				MyCartPage.showCart(primaryStage, userId, mainApp);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Show the cart page
        });

        // Tombol logout
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> mainApp.switchToLoginForm()); // Pindah ke halaman login
        logoutButton.setStyle("-fx-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: transparent");

        // Navbar
        HBox navBar = new HBox(10, logoContainer, searchField, searchButton, myCartButton, logoutButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(5, 30, 5, 30));
        navBar.setStyle("-fx-background-color: #2b2f3b; -fx-background-radius: 30;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        navBar.setMaxWidth(900);
        
        // Filter kategori
        Label filterLabel = new Label("Filter");
        filterLabel.setStyle("-fx-text-fill: #5a6478;");

        Label categoryLabel = new Label("Category");
        categoryLabel.setStyle("-fx-text-fill: #5a6478; -fx-padding: 5px;");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(categories); // Isi kategori
        categoryComboBox.setPromptText("Select a category");
        categoryComboBox.setStyle("-fx-background-color: #5a6478; -fx-font-weight: bold; -fx-text-fill: #5a6478; -fx-padding-left: 5px;");

        Button filterButton = new Button("Apply");
        filterButton.setStyle("-fx-font-weight: bold; -fx-background-color: orange; -fx-text-fill: white;");
        filterButton.setOnAction(event -> applyFilter(categoryComboBox.getValue()));
        
        productListLabel = new Label();
        productListLabel.setStyle("-fx-text-fill: #5a6478;");

        productListView = new ListView<>();
        productListView.setStyle("-fx-control-inner-background: #02000a; -fx-overflow-y: hidden; -fx-border-color: #02000a; -fx-border: none;");
        productListView.setPrefWidth(305); // ukuran lebar listview produk list
        productListView.setPrefHeight(425); // ukuran lebar listview produk list

        // Listener buat sembunyikan scrollbar setelah scene dimuat
        productListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                productListView.lookupAll(".scroll-bar").forEach(scrollBar -> {
                    scrollBar.setStyle("-fx-opacity: 0; -fx-control-inner-background: #02000a; -fx-max-width: 0; -fx-max-height: 0;");
                });
            }
        });
        
        // Design UI List View Product item
        productListView.setCellFactory(param -> new ListCell<Item>() {
            @Override // updateItem adalah built in method dari ListCell
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Layout untuk setiap item produk
                    HBox card = new HBox(10);
                    card.setStyle("-fx-background-color: #292d38; -fx-background-radius: 10px; -fx-padding: 15;");
                    card.setMaxWidth(300);

                    // Nama produk dan harga
                    Label nameLabel = new Label(item.getName());
                    nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;");

                    Label priceLabel = new Label("$" + item.getPrice());
                    priceLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: orange; -fx-font-weight: bold;");
                    
                    Label qtyLabel = new Label(item.getQuantity() + " Left");
                    // padding inline = jarak right n left, padding block = jarak top n bottom
                    qtyLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: red; -fx-padding-left: 3px; -fx-padding-right: 3px; -fx-padding-top: 2px; -fx-padding-bottom: 2px;");
                    qtyLabel.setPadding(new Insets(2,5,2,5));
                    
                    // Layout agar pricelabel dan qty label bersebelahan
                    HBox hb = new HBox(10);
                    hb.getChildren().addAll(priceLabel, qtyLabel);
                    hb.setAlignment(Pos.CENTER_LEFT);
                    
                    VBox cardDetail = new VBox(10);
                    cardDetail.getChildren().addAll(nameLabel, hb);

                    // Placeholder buat gambar produk
                    Rectangle productImagePlaceholder = new Rectangle(100, 100);
                    productImagePlaceholder.setFill(Color.GRAY);
                    productImagePlaceholder.setArcHeight(10);
                    productImagePlaceholder.setArcWidth(10);

                    card.getChildren().addAll(productImagePlaceholder, cardDetail);
                    
                    // set event handler ketika kartu di-klik
                    card.setOnMouseClicked(event-> DetailCardPage.showProductDetails(item, primaryStage, mainApp)); // redirect user ke halaman sesuai card yg di klik
                    
                    setGraphic(card);
                }
            }
        });

        productListView.getItems().addAll(allProducts); // Menyimpan semua produk ke list view yg nantinya akan ditampilkan


        // Layout  product list
        GridPane productListBox = new GridPane();
        productListBox.setPadding(new Insets(5));
        productListBox.add(productListLabel, 0, 0);
        productListBox.add(productListView, 0, 1);
        productListBox.setAlignment(Pos.CENTER);

        // Layou filter and category
//        GridPane categoryLayout = new GridPane();

        HBox filterBox = new HBox(10, categoryComboBox, filterButton);
        filterBox.setAlignment(Pos.TOP_CENTER);
        filterBox.setPadding(new Insets(0 ,10,10,10));
        filterBox.setMaxHeight(30);

//        categoryLayout.add(categoryLabel, 0, 0);
//        categoryLayout.add(filterBox, 0, 1);
//        categoryLayout.setAlignment(Pos.CENTER);
//        categoryLayout.setStyle("-fx-background-color: #2b2f3b; -fx-background-radius: 10px;");
        
        VBox vb = new VBox(10, categoryLabel, filterBox);
        vb.setAlignment(Pos.CENTER_LEFT);
        vb.setStyle("-fx-background-color: #2b2f3b; -fx-background-radius: 10px;");
        
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(5));
        gp.add(filterLabel, 0, 0);
        gp.add(vb, 0, 1);
        gp.setAlignment(Pos.TOP_CENTER);

        // Main layout
        HBox hb = new HBox();
        hb.setSpacing(10);
        hb.getChildren().addAll(gp, productListBox);
        hb.setAlignment(Pos.TOP_CENTER);

        VBox mainLayout = new VBox(10, navBar, titleContainer, hb);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #02000a;");

        BorderPane bp = new BorderPane();
        bp.setCenter(mainLayout);

        // Set scene
        Scene shopperScene = new Scene(bp, 900, 700);
        primaryStage.setTitle("Shopping Dashboard");
        primaryStage.setScene(shopperScene);
        primaryStage.show();

        return mainLayout;
    }

    public static void searchProducts(String query) {
    	if (query == null || query.trim().isEmpty()) {
			System.out.println("No search query entered!");
			return;
		}
    	
    	// memfilter produk based on yg dicari
        List<Item> filteredProducts = allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());


        productListView.getItems().clear();
        productListView.getItems().addAll(filteredProducts);

        System.out.println("Found " + filteredProducts.size() + " matching products.");
    }

    public void applyFilter(String category) {
        List<Item> filteredProducts;

        // Filter products based on category
        if ("All".equals(category)) {
            filteredProducts = allProducts; // Show all products if category is "All"
        } else {
            filteredProducts = allProducts.stream()
                    .filter(product -> product.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        // Update product list view with filtered products
        productListView.getItems().clear();
        productListView.getItems().addAll(filteredProducts);

        // Update the label text to show the filtered count
        String labelText = "Showing ";
        String productCountText = filteredProducts.size() + " products ";
        String categoryText = !"All".equals(category) ? "in '" + category + "' category" : "";

        // Create TextFlow for label text (we'll completely replace the content)
        Text showingText = new Text(labelText);
        showingText.setStyle("-fx-fill: #5a6478;");
        
        Text productCount = new Text(productCountText);
        productCount.setStyle("-fx-fill: orange;");
        
        Text categoryLabelText = new Text(categoryText);
        categoryLabelText.setStyle("-fx-fill: orange;");

        TextFlow textFlow = new TextFlow(showingText, productCount, categoryLabelText);
        
        // Replace the content of the label with the updated TextFlow
        productListLabel.setGraphic(textFlow);
    }


    // method untuk mengambil data item dari database yg akan ditampilkan melalui list view
    public static List<Item> fetchProductsFromDatabase() {
        try {
            return DatabaseSchema.getAllItems();
        } catch (SQLException e) {
            e.printStackTrace(); 
            return new ArrayList<>();
        }
    }
    
    public static List<String> fetchCategoriesFromDatabase() {
        try {
            // Ambil semua kategori produk dari database
            return DatabaseSchema.getAllCategories(); // Misalnya getAllCategories() adalah metode untuk mengambil kategori
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static String extractNameFromEmail(String email) {
        int index = email.indexOf("@");
        if (index != -1) {
            return email.substring(0, index);
        }
        return email; // Return the email itself if "@" is not found
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
