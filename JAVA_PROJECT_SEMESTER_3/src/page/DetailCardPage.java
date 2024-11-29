package page;


import java.sql.SQLException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import main.DatabaseSchema;
import main.DatabaseSchema.Item;
import main.Main; // Import Main class
import main.ShopperDashboard;

public class DetailCardPage {

    // Method untuk menampilkan halaman detail produk
    public static void showProductDetails(Item item, Stage primaryStage, Main mainApp) {
    	
        // Logo
        Label logo1 = new Label("Go Go");
        Label logo2 = new Label("Query");

        logo1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white;");
        logo2.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: orange; -fx-translate-y: -10px;");

        VBox logoContainer = new VBox(logo1, logo2);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setSpacing(-10);
        logoContainer.setOnMouseClicked(e -> mainApp.switchToShopperDashboard());


        // Kolom pencarian
        TextField searchField = new TextField();
        searchField.setPromptText("Search items in GoGoQuery Store");
        searchField.setStyle("-fx-background-color: #444c5e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        searchField.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.setPadding(new Insets(3, 10, 2, 10));
        searchButton.setOnAction(event -> ShopperDashboard.searchProducts(searchField.getText()));
        searchButton.setStyle("-fx-translate-x: -55; -fx-text-fill: white; -fx-border-color: transparent; -fx-background-color: #5f6f7f; -fx-font-weight: bold;");

        // Tombol keranjang
        Button myCartButton = new Button("My Cart");
        myCartButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
        myCartButton.setOnAction(event -> {
            // Redirect to MyCartPage ketika diklik
        	int userId = Main.getCurrentUserId();
            try {
				MyCartPage.showCart(primaryStage, userId, mainApp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        });

        // Tombol logout
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> mainApp.switchToLoginForm()); // Pindah ke halaman login
        logoutButton.setStyle("-fx-color: red; -fx-text-fill: white; -fx-font-weight: bold;");

        // Navbar
        HBox navBar = new HBox(10, logoContainer, searchField, searchButton, myCartButton, logoutButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(5, 30, 5, 30));
        navBar.setStyle("-fx-background-color: #2b2f3b; -fx-background-radius: 30;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        navBar.setMaxWidth(900);
    	

        // Nama produk
        Label productNameLabel = new Label(item.getName());
        productNameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Harga produk
        Label productPriceLabel = new Label("$" + item.getPrice());
        productPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: orange;");
        
        // Label category
        Label productCategoryLabel = new Label("Category :");
        productCategoryLabel.setStyle("-fx-text-fill: white;");
        Label productCategoryLabel2 = new Label(item.getCategory());
        productCategoryLabel2.setStyle("-fx-text-fill: orange;");
        HBox productCategoryBox = new HBox(10, productCategoryLabel,productCategoryLabel2);
        
        //label item detail
        Label itemDetailLabel = new Label("Item Detail");
        itemDetailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: orange; -fx-border-color: orange; -fx-border-width: 0 0 2px 0;");
        itemDetailLabel.setMaxWidth(200);

        Label specificationLabel = new Label("Specification: ");
        specificationLabel.setStyle("-fx-text-fill:white;");
        
     // Ganti Label dengan TextArea untuk deskripsi produk
        TextArea productDescTextArea = new TextArea(item.getDescription());
        productDescTextArea.setWrapText(true);  // Agar teks membungkus (wrap)
        productDescTextArea.setEditable(false);  // Hanya tampilkan teks (tidak bisa diubah)
        productDescTextArea.setStyle("-fx-text-fill: white; -fx-border-radius: 0px; -fx-control-inner-background: #02000a; -fx-border-color: transparent; -fx-border: none; -fx-background-color: transparent;");
        productDescTextArea.setMaxWidth(250); // Lebar maksimal sesuai dengan layout
        productDescTextArea.setMaxHeight(500);
        productDescTextArea.setPrefRowCount(10); // Atur jumlah baris teks default
       
        
        // Best Seller Label
        Label bestSellerLabel = new Label("Best Seller!");
        bestSellerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: purple; -fx-background-radius: 5px;");
        bestSellerLabel.setPadding(new Insets(5));
        bestSellerLabel.setAlignment(Pos.CENTER_LEFT);
        
        // pakai stackpane agar background best seller memanjang kekanan
        StackPane bestSellerContainer = new StackPane(bestSellerLabel);
        bestSellerContainer.setStyle("-fx-background-color: purple; -fx-background-radius: 5px;");
        bestSellerContainer.setPadding(new Insets(5)); // Tambahkan padding agar background terlihat
        
        // item quantity label
        Label itemQtyLabel = new Label("Set item quantity");
        itemQtyLabel.setStyle("-fx-text-fill: white;");
        
        // Spinner 
        int MaxItemStock = item.getQuantity(); // max qty yg dinamis, jadi tergantung stock stiap produk
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, MaxItemStock,1);
        
        // setting value factory ke spinner
        spinner.setValueFactory(valueFactory);
        
        // mencegah perubahan ketika user sedang menggunakan spinner
        spinner.setEditable(false);
        
        // Stock information
        Label stockLabel = new Label("Stock :");
        stockLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label amountQtyLabel = new Label(String.valueOf(item.getQuantity())); // convert int ke string agar nnti bisa diganti warnanya ke orange sesuai case.
        amountQtyLabel.setStyle("-fx-text-fill: orange;");
        
        HBox stockBox = new HBox(10, stockLabel, amountQtyLabel); // jadinya "Stock : 8" misal
        stockBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox spinnerNstockBox = new HBox(10, spinner, stockBox); // spinner sblhnya Stock : 8
        spinnerNstockBox.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("Add to Cart");
        addBtn.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: orange;");
        addBtn.setMaxWidth(Double.MAX_VALUE); // button akan memanjang
        addBtn.setAlignment(Pos.CENTER);
        
     // Ketika tombol Add to Cart ditekan
        addBtn.setOnAction(event -> {
			try {
				handleAddToCart(item, spinner, mainApp);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}); 
        
        VBox itemQtyBox = new VBox(10, itemQtyLabel, spinnerNstockBox, addBtn);
        itemQtyBox.setStyle("-fx-background-color: #02000a;-fx-border: 2px; -fx-border-color: grey; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        itemQtyBox.setAlignment(Pos.CENTER_LEFT);
        itemQtyBox.setPadding(new Insets(15,15,15,15));
        
        VBox bestSellerNitemQtyBox = new VBox(10, bestSellerContainer, itemQtyBox);

        // Placeholder gambar produk
        Rectangle productImagePlaceholder = new Rectangle(150, 150); // Ukuran placeholder gambar
        productImagePlaceholder.setFill(Color.GRAY); // Gambar abu-abu
        productImagePlaceholder.setArcHeight(10); // Sudut melengkung
        productImagePlaceholder.setArcWidth(10);
        productImagePlaceholder.setStyle("-fx-margin-top: 20px;");

        // Layout untuk halaman detail produk
        VBox productDetailsLayout = new VBox(10, productNameLabel, productPriceLabel, productCategoryBox, itemDetailLabel, specificationLabel, productDescTextArea);
        productDetailsLayout.setAlignment(Pos.CENTER_LEFT); // konten detail card pada page di tengah
        
        HBox contentLayout = new HBox(25,productImagePlaceholder, productDetailsLayout, bestSellerNitemQtyBox); // flex modelnya image bakal di kiri dan informasi vertikal
        contentLayout.setAlignment(Pos.TOP_CENTER);
        
        VBox vb = new VBox(30,navBar, contentLayout);
        vb.setPadding(new Insets(20));
        vb.setAlignment(Pos.TOP_CENTER);
        
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #02000a;");
        bp.setCenter(vb);

        // Bikin scene baru untuk halaman detail produk
        Scene productDetailsScene = new Scene(bp, 900, 700);
        primaryStage.setScene(productDetailsScene); // Ganti scene ke halaman detail produk
    }
    
    // Method yg Menangani tombol Add to Cart dengan menyimpan item ke database.
	public static void handleAddToCart(DatabaseSchema.Item item, Spinner<Integer> spinner, Main mainApp) throws SQLException {
	    int selectedQuantity = spinner.getValue();
	    int currentStock = item.getQuantity();
	
	    // fetch user id
	    int userId = Main.getCurrentUserId();
	
	    // validasi userId
	    if (userId == 0) {
	        ShopperDashboard.showAlert(Alert.AlertType.ERROR, "Error", "User not logged in!");
	        return;
	    }
	
	    // Cek if item udh di cart menggunakan method isItemInCart yg ada di class DatabaseSchema
	    int existingQuantity = DatabaseSchema.isItemInCart(userId, item.getId());
	
	    if (existingQuantity == -1) {
	        // If item is not in the cart, add it
	        if (selectedQuantity > currentStock) {
	            ShopperDashboard.showAlert(Alert.AlertType.WARNING, "Stock Limit Exceeded", "The selected quantity exceeds available stock!");
	        } else {
	            boolean success = DatabaseSchema.addToCart(userId, item.getId(), selectedQuantity);
	            if (success) {
	                ShopperDashboard.showAlert(Alert.AlertType.INFORMATION, "Item Added", "Item successfully added to your cart.");
	                // Update cart list view
	                MyCartPage.updateCartListView();
	            } else {
	                ShopperDashboard.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add item to cart.");
	            }
	        }
	    } else {
	        // if item udh ada di cart, update quantity
	        int newQuantity = existingQuantity + selectedQuantity;
	        if (newQuantity > currentStock) {
	            ShopperDashboard.showAlert(Alert.AlertType.WARNING, "Stock Limit Exceeded", "The total quantity in the cart exceeds available stock!");
	        } else {
	            boolean success = DatabaseSchema.updateCartQuantity(userId, item.getId(), newQuantity);
	            if (success) {
	                ShopperDashboard.showAlert(Alert.AlertType.INFORMATION, "Item Updated", "Item quantity updated in your cart.");
	                // Memperbaharui list view cart
	                MyCartPage.updateCartListView();
	            } else {
	                ShopperDashboard.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update item in cart.");
	            }
	        }
	    }
	}

	// Method yg ngehandle update quantity
    public static void handleUpdateQuantity(Item item, int newQuantity) {
        if (newQuantity <= 0) {
            // Jangan izinkan kuantitas menjadi negatif atau nol
            ShopperDashboard.showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Quantity must be greater than 0.");
            return;
        }

        // Update kuantitas item di database
        boolean success = DatabaseSchema.updateCartQuantity(Main.getCurrentUserId(), item.getId(), newQuantity);
        if (success) {
            ShopperDashboard.showAlert(Alert.AlertType.INFORMATION, "Quantity Updated", "Item quantity updated.");
        } else {
            ShopperDashboard.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update item quantity.");
        }
    }
    
    public static void handleRemoveItemFromCart(Item item) {
        // Menghapus item dari keranjang di database
        boolean success = DatabaseSchema.removeItemFromCart(Main.getCurrentUserId(), item.getId());
        if (success) {
            ShopperDashboard.showAlert(Alert.AlertType.INFORMATION, "Item Removed", "Item removed from your cart.");
        } else {
            ShopperDashboard.showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove item from cart.");
        }
    }



    
}
