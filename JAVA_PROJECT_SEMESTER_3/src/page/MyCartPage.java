package page;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.DatabaseSchema;
import main.DatabaseSchema.Item;
import main.Main;
import main.ShopperDashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyCartPage {
	private static Label searchResultsLabel;
    private static ObservableList<Item> cartItems = FXCollections.observableArrayList(); // Ambil data cart dari database
    private static Label totalAmountLabel; // Label untuk menampilkan Grand Total
    private static ListView<HBox> cartListView; // komponen tampilan list cart item dalam bentuk HBox
    
    // fungsi untuk menampilkan halaman cart
    public static void showCart(Stage primaryStage, int userId, Main mainApp) throws SQLException {
    	cartItems.setAll(fetchCartItemsFromDatabase());
    	
        // Bikin pesan sambutan
        String userEmail = mainApp.getUserEmail();
        String username = ShopperDashboard.extractNameFromEmail(userEmail);
        Label titleLabel = new Label(username);
        Label titleLabel2 = new Label("'s Cart");


        // Set gaya untuk pesan sambutan
        titleLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: normal; -fx-font-style: italic; -fx-text-fill: white; -fx-translate-x: -90px; -fx-translate-y: 6px");
        titleLabel2.setStyle("-fx-font-size: 40px; -fx-font-weight: normal; -fx-font-style: italic; -fx-text-fill: orange; -fx-translate-x: -86px; -fx-translate-y: 6px");

        HBox titleContainer = new HBox(titleLabel, titleLabel2);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setStyle("-fx-translate-x: -170px;");
        titleContainer.setPadding(new Insets(20, 0, 0, 0));

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
        searchButton.setStyle("-fx-translate-x: -55; -fx-text-fill: white; -fx-border-color: transparent; -fx-background-color: #5f6f7f; -fx-font-weight: bold;");
        searchButton.setOnAction(event -> {
        	String query = searchField.getText();
        	searchCartProducts(query);
        });
        

        // Tombol keranjang
        Button myCartButton = new Button("My Cart");
        myCartButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");

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

        // label indikator jumlah produk yg telah disearch
        searchResultsLabel = new Label();
        searchResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #5a6478; -fx-translate-x: 20px;");
        searchResultsLabel.setAlignment(Pos.CENTER_LEFT);
        
        // Cart List View
        cartListView = new ListView<>();
        cartListView.setStyle("-fx-control-inner-background: #02000a; -fx-overflow-y: hidden; -fx-border-color: #02000a; -fx-border: none; -fx-padding: 15px;");
        cartListView.getItems().addAll(createCartItemsUI(cartItems));
        
        VBox cartListViewBox = new VBox(1, searchResultsLabel, cartListView);
        cartListViewBox.setAlignment(Pos.CENTER_LEFT);
        
     // Placeholder saat keranjang kosong
        Label emptyPlaceholder = new Label("Your cart is empty");
        emptyPlaceholder.setStyle(
            "-fx-font-size: 24px;" + 
            "-fx-font-style: italic;" + 
            "-fx-text-fill: #888;"
        );
        cartListView.setPlaceholder(emptyPlaceholder);
        
        // Listener buat sembunyikan scrollbar setelah scene dimuat
        cartListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
            	cartListView.lookupAll(".scroll-bar").forEach(scrollBar -> {
                    scrollBar.setStyle("-fx-opacity: 0; -fx-control-inner-background: #02000a; -fx-max-width: 0; -fx-max-height: 0;");
                });
            }
        });

        
        // billing summary label
        Label billSummaryLabel = new Label("Billing summary");
        billSummaryLabel.setStyle("-fx-text-fill: white;");

        // Total amount information
        Label totalLabel = new Label("Total :");
        totalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Menampilkan grand total langsung di label
        totalAmountLabel = new Label("$" + String.format("%.2f", getGrandTotal())); // Grand total langsung ditampilkan di sini
        totalAmountLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 18px;");

        HBox totalNtotalAmountBox = new HBox(10, totalLabel, totalAmountLabel); // Jadinya "Total : [grand total price]"
        totalNtotalAmountBox.setAlignment(Pos.CENTER_LEFT);

        Label taxDescInformation = new Label("*Tax and delivery cost included");
        taxDescInformation.setStyle("-fx-font-weight: bold; -fx-text-fill: grey; -fx-border-color: white; -fx-border-width: 0 0 2px 0;");

        VBox totalAmountNdescBox = new VBox(5, totalNtotalAmountBox, taxDescInformation);
        totalAmountNdescBox.setStyle("-fx-font-weight: bold; -fx-text-fill: grey; -fx-border-color: grey; -fx-border-width: 0 0 2px 0;");

        Button coBtn = new Button("Checkout Items");
        coBtn.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: orange;");
        coBtn.setMaxWidth(Double.MAX_VALUE); // Button akan memanjang
        coBtn.setAlignment(Pos.CENTER);
        coBtn.setOnAction(e -> handleCheckoutItem());

        
        VBox itemQtyBox = new VBox(10, billSummaryLabel, totalAmountNdescBox, coBtn);
        itemQtyBox.setStyle("-fx-background-color: #02000a; -fx-border: 2px; -fx-border-color: grey; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        itemQtyBox.setAlignment(Pos.TOP_CENTER);
        itemQtyBox.setPadding(new Insets(15, 15, 15, 15));

        HBox cardNTotalInformation = new HBox(20, cartListViewBox,itemQtyBox);
        cardNTotalInformation.setAlignment(Pos.TOP_CENTER);
        cardNTotalInformation.setMaxWidth(Double.MAX_VALUE); // Memperluas lebar maksimum
        HBox.setHgrow(cartListView, Priority.ALWAYS);
        HBox.setHgrow(cartListViewBox, Priority.ALWAYS);
        cardNTotalInformation.setStyle("-fx-background-color: #02000a; -fx-padding: 20px;"); 

        VBox cartContentBox = new VBox(5, titleContainer, cardNTotalInformation);
        cartContentBox.setAlignment(Pos.CENTER);
        
        // Adding components to the layout
        VBox cartPageLayout = new VBox(20);
        cartPageLayout.getChildren().addAll(navBar, cartContentBox);
        cartPageLayout.setStyle("-fx-background-color: #02000a; -fx-padding: 20px;");
        cartPageLayout.setAlignment(Pos.TOP_CENTER);        
        
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #02000a;");
        bp.setCenter(cartPageLayout);

        Scene cartScene = new Scene(bp, 900, 700);
        primaryStage.setTitle("My Cart Page");
        primaryStage.setScene(cartScene);
        primaryStage.show();
        
        // Update Grand Total setiap kali tampilan dimuat
        updateGrandTotal(); 
    }

    // Fungsi untuk mengambil produk dari database untuk cart (misalnya berdasarkan userId)
    public static List<Item> fetchCartItemsFromDatabase() {
        try {
            List<Item> items = DatabaseSchema.getCartItemsByUserId(Main.getCurrentUserId());
            System.out.println("Cart items fetched: " + items.size());  // Debugging line
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // Fungsi untuk menghitung grand total
    public static double getGrandTotal() {
        double grandTotal = 0;
        for (Item item : cartItems) {
            grandTotal += item.getPrice() * item.getStock();
        }
        // Membulatkan ke dua angka di belakang koma
        return Math.round(grandTotal * 100.0) / 100.0;
    }


    // Fungsi untuk mengupdate Grand Total pada Label
    public static void updateGrandTotal() {
        totalAmountLabel.setText("$" + getGrandTotal()); // Update label total dengan harga baru
    }

    // Fungsi untuk membuat UI item di cart
    public static List<HBox> createCartItemsUI(List<Item> items) throws SQLException {
        List<HBox> cartItemUI = new ArrayList<>();
        for (Item item : items) {
            HBox cartItem = new HBox(10);
            cartItem.setStyle("-fx-background-color: #292d38; -fx-background-radius: 10px; -fx-padding: 10px;");
            cartItem.setMaxWidth(650);

            // Placeholder untuk gambar produk
            Rectangle productImage = new Rectangle(100, 100);
            productImage.setFill(Color.GRAY);
            productImage.setArcHeight(10);
            productImage.setArcWidth(10);

            // Nama produk
            Label nameLabel = new Label(item.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

            // Harga produk
            Label priceLabel = new Label("$" + item.getPrice());
            priceLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: orange; -fx-font-weight: bold;");

            // agar name dan price atas bawah
            VBox nameNpriceContainer = new VBox(5, nameLabel, priceLabel);
            nameNpriceContainer.setAlignment(Pos.TOP_LEFT);

            int MaxQty = getMaxQtyFromDatabase(item.getId());
            Spinner<Integer> quantitySpinner = new Spinner<>(1, MaxQty, item.getStock()); // Main 1, max 100, default quantity
            quantitySpinner.setStyle("-fx-background-color: #444c5e; -fx-text-fill: white;");
            
            quantitySpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue > MaxQty) {
                    quantitySpinner.getValueFactory().setValue(MaxQty); // Enforce the max value
                    showAlert(Alert.AlertType.INFORMATION, "Quantity Limit Reached", 
                              "You cannot exceed the maximum quantity of " + MaxQty + " items.");
                } else {
                    item.setStock(newValue); // Update item quantity di cart
                    updateGrandTotal(); //update total price
                }
            });


            // Tombol X untuk menghapus item
            Button removeButton = new Button("X");
            removeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
            removeButton.setOnAction(event -> handleRemoveItem(item));

            VBox spinnerNremoveBtnContainer = new VBox(10, removeButton, quantitySpinner);
            spinnerNremoveBtnContainer.setAlignment(Pos.CENTER_RIGHT);

            HBox itemContainer = new HBox(20, productImage, nameNpriceContainer);
            itemContainer.setAlignment(Pos.CENTER_LEFT);
            
            HBox itemQtyBoxContainer = new HBox(spinnerNremoveBtnContainer);
            itemQtyBoxContainer.setAlignment(Pos.CENTER_RIGHT);
            
            Region spacer = new Region(); // mirip dengan 'space-between' pada css/tailwindcss, guna region adalah untuk memisahkan (foto+name+price) dengan (remove+spinnerQty)
            HBox.setHgrow(spacer, Priority.ALWAYS); // Membuat spacer mengisi ruang kosong

            HBox cardContainer = new HBox(10, itemContainer, spacer, itemQtyBoxContainer);
            cardContainer.setStyle("-fx-padding: 10px; -fx-background-color: #292d38; -fx-background-radius: 10px;");

            
            cartItemUI.add(cardContainer);
        }
        return cartItemUI;
    }

    // Fungsi untuk menangani klik tombol Remove Item
    public static void handleRemoveItem(Item item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Are you sure you want to remove " + item.getName() + " from your cart?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Remove item from the ObservableList (this will trigger ListView update)
                cartItems.remove(item); // Remove item from cartItems
                // Update the database
                DatabaseSchema.removeItemFromCart(item.getId(), Main.getCurrentUserId());
                
                // Update the grand total (this will update the label)
                updateGrandTotal();

                // Refresh ListView to show updated cart (clear and re-add)
                cartListView.getItems().clear();  // Clear the ListView
                try {
					cartListView.getItems().addAll(createCartItemsUI(cartItems));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Re-add updated items
            }
        });
    }

    // method untuk menangani checkout dari cart
    public static void handleCheckoutItem() {
        int userId = Main.getCurrentUserId(); // Get the userId of the current user

        // kondisi jika cart items shopper kosong yg ketika shopper klik checkout maka nampilin warning
        if (cartItems.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Cart is Empty");
            alert.setHeaderText("You cannot proceed with checkout because your cart is empty.");
            alert.showAndWait();
            return;
        }

        // try catch untuk menangani produk jika ada
        try {
            // Step 1: ngegenerate transaction header
            int transactionId = DatabaseSchema.createTransactionHeader(userId);
            if (transactionId == -1) {
                // Handle error if TransactionID is not generated
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Checkout Failed");
                alert.setHeaderText("An error occurred while processing your transaction.");
                alert.showAndWait();
                return;
            }

            // Step 2: ngegenerate transactiondetail
            DatabaseSchema.createTransactionDetails(transactionId, cartItems);

            // Step 3: clear cart setelah checkout
            DatabaseSchema.clearUserCart(userId);
            
            // Step 4: mengurangi stock/quantity item yg ada di database 
            for (Item item : cartItems) {
                DatabaseSchema.updateItemStock(item);
            }


            // Step 5: Success information
            Alert successAlert = new Alert(AlertType.INFORMATION);
            successAlert.setTitle("Checkout Successful");
            successAlert.setHeaderText("Your order has been successfully placed.");
            successAlert.setContentText("Transaction ID: " + transactionId + "\nTotal: $" + getGrandTotal());
            successAlert.showAndWait();

            // Step 6: Seluruh item pada cart, dan total harga cart akan direset
            cartItems.clear(); // Clear Cart
            updateCartListView(); // Mengupdate item ke semula
            updateGrandTotal(); // Mengupdate total item pada cart ke semula

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Checkout Error");
            alert.setHeaderText("An error occurred during checkout.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
 // Fungsi untuk mengambil stok produk dari database
    public static int getMaxQtyFromDatabase(int itemId) throws SQLException {
        // Fetch the stock for the specific item from the database
        int stock = DatabaseSchema.getItemStock(itemId); // Implement this method to return stock based on itemId
        
        return stock;
    }

   // Method untuk logika searchbar pada my cart page
   public static void searchCartProducts(String query) {
    if (query == null || query.trim().isEmpty()) {
        // Jika search bar kosong, tampilkan semua produk di cart
        Platform.runLater(() -> {
            try {
                cartListView.getItems().clear(); // Clear daftar item yang ada
                cartListView.getItems().addAll(createCartItemsUI(cartItems)); // Tambahkan semua item kembali
                // Update label jumlah produk
                String labelText = "Showing ";
                String productCountText = cartItems.size() + "";
                String productInfoText = " products in cart";

                Text showingText = new Text(labelText);
                showingText.setStyle("-fx-fill: #5a6478;");

                Text productCount = new Text(productCountText);
                productCount.setStyle("-fx-fill: orange;");

                Text productInfo = new Text(productInfoText);
                productInfo.setStyle("-fx-fill: #5a6478;");

                TextFlow textFlow = new TextFlow(showingText, productCount, productInfo);

                // Update label untuk menunjukkan jumlah produk yang ditemukan
                searchResultsLabel.setGraphic(textFlow); // Pastikan ada label untuk menampilkan hasil pencarian
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    } else {
        // Jika search bar tidak kosong, tampilkan produk yang sesuai dengan query
        List<Item> filteredProducts = cartItems.stream()
                .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        // Update ListView dengan hasil pencarian
        Platform.runLater(() -> {
            cartListView.getItems().clear();
            try {
                cartListView.getItems().addAll(createCartItemsUI(filteredProducts));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Menambahkan label yang menunjukkan jumlah produk yang ditemukan
            String labelText = "Showing ";
            String productCountText = filteredProducts.size() + "";
            String productInfoText = " products in cart";

            Text showingText = new Text(labelText);
            showingText.setStyle("-fx-fill: #5a6478;");

            Text productCount = new Text(productCountText);
            productCount.setStyle("-fx-fill: orange;");

            Text productInfo = new Text(productInfoText);
            productInfo.setStyle("-fx-fill: #5a6478;");

            TextFlow textFlow = new TextFlow(showingText, productCount, productInfo);

            // Menampilkan informasi stock product ketika shopper mencari menggunakan searchbar
            searchResultsLabel.setGraphic(textFlow); // Pastikan ada label untuk menampilkan hasil pencarian

            // Menampilkan placeholder jika hasil pencarian kosong
            if (filteredProducts.isEmpty()) {
                Label emptyLabel = new Label("No items found in your cart.");
                emptyLabel.setStyle("-fx-font-style: italic; -fx-font-size: 20px; -fx-text-fill: lightgray;");
                cartListView.setPlaceholder(emptyLabel); // Tampilkan placeholder saat kosong
            }
        });

        System.out.println("Found " + filteredProducts.size() + " matching products in the cart.");
    }
}



    // Helper untuk menampilkan alert
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    public static void updateCartListView() throws SQLException {
        // Fetch updated cart items from the database
        List<Item> updatedCartItems = DatabaseSchema.getCartItemsForUser(Main.getCurrentUserId());

        Platform.runLater(() -> {
            if (cartListView != null && cartListView.getItems() != null) {
                cartListView.getItems().clear();  // Clear items in the ListView
                try {
					cartListView.getItems().addAll(createCartItemsUI(cartItems));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Add updated items
            }
        });

    }


}
