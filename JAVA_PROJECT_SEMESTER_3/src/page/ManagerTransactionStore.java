package page;

import java.util.Date;


// FIle ini untuk localStorage menu management queue oleh manager
public class ManagerTransactionStore {
    private int transactionId;
    private int customerId;
    private String customerEmail;
    private Date date;
    private double total;  // Total dihitung di Java
    private String status;

    // Constructor lengkap untuk ManagerTransactionStore
    public ManagerTransactionStore(int transactionId, int customerId, String customerEmail, Date date, double total, String status) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.date = date;
        this.total = total;
        this.status = status;
    }

    // Getter dan Setter untuk transactionId
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    // Getter dan Setter untuk customerId
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    // Getter dan Setter untuk customerEmail
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    // Getter dan Setter untuk date
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Getter dan Setter untuk total
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Getter dan Setter untuk status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Method to display transaction details
    @Override
    public String toString() {
        return "TransactionID: " + transactionId + ", CustomerID: " + customerId + 
               ", Email: " + customerEmail + ", Date: " + date + ", Status: " + status + 
               ", Total: " + total;
    }
}
