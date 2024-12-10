package main;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import page.ManagerTransactionStore;

public class DatabaseSchema {
    public static final String URL = "jdbc:mysql://localhost:3306/gogoquery";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "xampp";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static boolean isEmailUnique(String email) {
        String query = "SELECT COUNT(*) FROM MsUser WHERE UserEmail = ?";
        try (Connection connection = getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0; // Mengembalikan true jika tidak ada email yang cocok
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false jika terjadi kesalahan atau email ditemukan
    }


    public static boolean insertUser( LocalDate dob,String email, String password, boolean isMale, boolean isFemale, String role) {
        String insertQuery = "INSERT INTO MsUser (UserDOB,UserEmail, UserPassword, UserGender, UserRole ) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

        	preparedStatement.setDate(1, Date.valueOf(dob));
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, isMale ? "Male" : "Female");
            preparedStatement.setString(5, role);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // READ user id
    public static int getUserIdByEmail(String email) {
    	String query = "SELECT UserId FROM MsUser WHERE UserEmail = ?"; // mencari id yg emailnya ...
    	try  (Connection connection = getConnection();
    	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
            	if (resultSet.next()) {
					return resultSet.getInt("UserID");
				}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return -1; // user not found
    }
    

    // READ all user ids
    public static List<Integer> getAllUserIds() {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT UserID FROM MsUser"; // Query untuk mengambil semua UserID

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                userIds.add(resultSet.getInt("UserID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userIds; // Mengembalikan daftar UserID
    }


    // Menambahkan Item ke dalam Keranjang
    public static boolean addToCart(int userId, int itemId, int quantity) {
        int existingQuantity = isItemInCart(userId, itemId);
        if (existingQuantity > 0) {
            return updateCartQuantity(userId, itemId, existingQuantity + quantity);
        }
        String query = "INSERT INTO mscart (UserID, ItemID, Quantity) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, itemId);
            preparedStatement.setInt(3, quantity);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Update Quantity Item dalam Keranjang
    public static boolean updateCartQuantity(int userId, int itemId, int quantity) {
        String query = "UPDATE mscart SET Quantity = ? WHERE UserID = ? AND ItemID = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, itemId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Item> getCartItemsForUser(int userId) throws SQLException {
        List<Item> cartItems = new ArrayList<>();
        
        // Query SQL untuk mendapatkan data cart berdasarkan UserID
        String query = "SELECT mscart.UserID, mscart.ItemID, mscart.Quantity, msitem.ItemName, msitem.ItemPrice " +
                       "FROM mscart " +
                       "JOIN msitem ON mscart.ItemID = msitem.ItemID " +
                       "WHERE mscart.UserID = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);  // Set userId pada parameter pertama query
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int itemId = rs.getInt("ItemID");
                String name = rs.getString("ItemName");
                double price = rs.getDouble("ItemPrice");
                int quantity = rs.getInt("Quantity");
                
                // Menggunakan nilai default jika description dan category tidak tersedia
                String description = "No description available";  // Nilai default
                String category = "No category";  // Nilai default
                
                // Buat objek Item dengan parameter lengkap
                Item item = new Item(itemId, name, price, description, category, quantity);  
                cartItems.add(item);
            }

        }
        
        return cartItems;
    }
    
    // method untuk remove produk dari cart 
    public static boolean removeItemFromCart(int itemId, int userId) {
        String query = "DELETE FROM mscart WHERE UserID = ? AND ItemID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//    // Method untuk mengurangi quantity ketika 
//    public static boolean decreaseQuantityFromCart()

    // Mengecek apakah Item sudah ada dalam Keranjang
    public static int isItemInCart(int userId, int itemId) {
        String query = "SELECT Quantity FROM mscart WHERE UserID = ? AND ItemID = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, itemId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Item tidak ditemukan
    }

    // Membuat Transaksi
    public static boolean createTransaction(int userId, List<Item> cartItems) {
        String transactionQuery = "INSERT INTO transactionheader (UserID, DateCreated, Status) VALUES (?, NOW(), 'In Queue')";
        try (Connection connection = getConnection();
             PreparedStatement transactionStmt = connection.prepareStatement(transactionQuery, Statement.RETURN_GENERATED_KEYS)) {

            transactionStmt.setInt(1, userId);
            int rowsAffected = transactionStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Dapatkan TransactionID yang baru saja dibuat
                try (ResultSet generatedKeys = transactionStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int transactionId = generatedKeys.getInt(1);

                        // Menambahkan detail transaksi
                        for (Item item : cartItems) {
                            addTransactionDetail(transactionId, item);
                        }

                        return true;  // Transaksi berhasil
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Gagal membuat transaksi
    }

    // Menambahkan Detail Transaksi ke tabel transactiondetail
    private static void addTransactionDetail(int transactionId, Item item) {
        String query = "INSERT INTO transactiondetail (TransactionID, ItemID, Quantity) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, transactionId);
            preparedStatement.setInt(2, item.getId());
            preparedStatement.setInt(3, item.getQuantity());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Logic manager menambahkan item 
    public static boolean addItem(String name, double price, String description, String category, int stock) {
        String query = "INSERT INTO msitem (ItemName, ItemPrice, ItemDesc, ItemCategory, ItemQuantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, category);
            preparedStatement.setInt(5, stock);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Logic manager untuk memperbaharui item
    public static boolean updateItem(int itemId, String name, double price, String description, String category, int stock) {
        String query = "UPDATE msitem SET ItemName = ?, ItemPrice = ?, ItemDesc = ?, ItemCategory = ?, ItemQuantity = ? WHERE ItemID = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, category);
            preparedStatement.setInt(5, stock);
            preparedStatement.setInt(6, itemId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    START MANAGER SECTION
 

    // Method yg akan digunakan untuk nantinya terdisplay pada management queue page (Manager Dashboard)
    public static List<ManagerTransactionStore> getTransactionDetails(List<Integer> userIds) {
        List<ManagerTransactionStore> transactions = new ArrayList<>();

        if (userIds == null || userIds.isEmpty()) {
            return transactions; // Jika tidak ada UserId, langsung kembalikan list kosong
        }

        // Bangun query dinamis untuk IN clause
        String placeholders = String.join(",", userIds.stream().map(id -> "?").collect(Collectors.toList()));
        String query = "SELECT th.UserID, " +
                       "u.UserEmail, " +
                       "th.TransactionID, " +
                       "th.DateCreated, " +
                       "th.Status, " +
                       "SUM(td.Quantity * mi.ItemPrice) AS Total " +
                       "FROM transactionheader th " +
                       "JOIN transactiondetail td ON th.TransactionID = td.TransactionID " +
                       "JOIN msitem mi ON td.ItemID = mi.ItemID " +
                       "JOIN msuser u ON th.UserID = u.UserID " +
                       "WHERE th.UserID IN (" + placeholders + ") " + 
                       "GROUP BY th.UserID, th.TransactionID, th.DateCreated, th.Status, u.UserEmail"; 

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Isi parameter untuk IN clause
            for (int i = 0; i < userIds.size(); i++) {
                preparedStatement.setInt(i + 1, userIds.get(i)); // Set parameter IN
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("UserID");
                    String userEmail = resultSet.getString("UserEmail");
                    int transactionId = resultSet.getInt("TransactionID");
                    Date dateCreated = resultSet.getDate("DateCreated");
                    String status = resultSet.getString("Status");
                    double total = resultSet.getDouble("Total");

                    // Menambahkan hasil transaksi ke dalam list transaksi
                    transactions.add(new ManagerTransactionStore(transactionId, userId, userEmail, dateCreated, total, status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }


//    END MANAGER SECTION


    // Update Stok Item setelah Checkout
    public static boolean updateItemStock(Item item) {
        String query = "UPDATE msitem SET ItemQuantity = ItemQuantity - ? WHERE ItemID = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, item.getQuantity());
            preparedStatement.setInt(2, item.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateSendPackageStatus(int transactionId) {
        String query = "UPDATE TransactionHeader SET Status = 'Sent' WHERE TransactionID = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, transactionId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;  // Return true if rows are affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    

    // Mengambil semua Item
    public static List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM msitem"; // Ambil semua data dari msitem

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ItemID");
                String name = resultSet.getString("ItemName");
                double price = resultSet.getDouble("ItemPrice");
                String desc = resultSet.getString("ItemDesc");
                String category = resultSet.getString("ItemCategory");
                int quantity = resultSet.getInt("ItemQuantity");

                items.add(new Item(id, name, price, desc, category, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public static List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        
        String query = "SELECT DISTINCT ItemCategory FROM msitem"; // Misalnya, ambil kategori dari kolom ItemCategory
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("ItemCategory"));
            }
        }
        
        return categories;
    }
    
    public static int getItemStock(int itemId) throws SQLException {
        int maxQuantity = 0;
        String query = "SELECT ItemQuantity FROM msitem WHERE ItemID = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, itemId); // memasukkan ItemId ke query "?"
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    maxQuantity = rs.getInt("ItemQuantity");
                }
            }
        }
        
        return maxQuantity; // Return the stock quantity for the specific item
    }

    
    // Fungsi untuk mengambil daftar cart items berdasarkan userId
    public static List<Item> getCartItemsByUserId(int userId) throws SQLException {
	    List<Item> cartItems = new ArrayList<>();
	    
	    // Query SQL untuk mendapatkan data cart berdasarkan UserID
	    String query = "SELECT mscart.UserID, mscart.ItemID, mscart.Quantity, msitem.ItemName, msitem.ItemPrice " +
	                   "FROM mscart " +
	                   "JOIN msitem ON mscart.ItemID = msitem.ItemID " +
	                   "WHERE mscart.UserID = ?";
	    
	    try (Connection conn = getConnection(); 
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setInt(1, userId);  // Set userId pada parameter pertama query
	        
	        ResultSet rs = stmt.executeQuery();
	        
	        while (rs.next()) {
	            int itemId = rs.getInt("ItemID");
	            String name = rs.getString("ItemName");
	            double price = rs.getDouble("ItemPrice");
	            int quantity = rs.getInt("Quantity");
	            
	            // Menggunakan nilai default jika description dan category tidak tersedia
	            String description = "No description available";  // Nilai default
	            String category = "No category";  // Nilai default
	            
	            // Buat objek Item dengan parameter lengkap
	            Item item = new Item(itemId, name, price, description, category, quantity);  
	            cartItems.add(item);
	        }
	
	    }
	    
	    return cartItems;
	}
    
    public static int createTransactionHeader(int userId) throws SQLException {
        Connection conn = getConnection(); // Assuming you have a getConnection() method
        String query = "INSERT INTO transactionheader (UserID, DateCreated, Status) VALUES (?, CURRENT_DATE, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, "In Queue"); // Status can be "Pending", "Completed", etc.
            stmt.executeUpdate();
            
            // Get the generated TransactionID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated TransactionID
            }
            return -1; // If no ID was generated
        }
    }

    // Method to insert transaction details
    public static void createTransactionDetails(int transactionId, List<Item> cartItems) throws SQLException {
        Connection conn = getConnection(); // Assuming you have a getConnection() method
        String query = "INSERT INTO transactiondetail (TransactionID, ItemID, Quantity) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Item item : cartItems) {
                stmt.setInt(1, transactionId);
                stmt.setInt(2, item.getId()); // ItemID
                stmt.setInt(3, item.getQuantity()); // Quantity
                stmt.addBatch();
            }
            stmt.executeBatch(); // Execute all the insertions at once
        }
    }

    // Method to remove items from cart after checkout (optional)
    public static void clearUserCart(int userId) throws SQLException {
        Connection conn = getConnection();
        String query = "DELETE FROM mscart WHERE UserID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    
    
    public static class Item {
        private int id;
        private String name;
        private double price;
        private String description;
        private String category;
        private int quantity;

        public Item(int id, String name, double price, String description, String category, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.description = description;
            this.category = category;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }

        public String getCategory() {
            return category;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public int getStock() {
            return this.quantity; // Mengembalikan jumlah stok dari item
        }
    }

}
