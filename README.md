# GoGoQuery Application

### Project Description
**GoGoQuery** is a JavaFX-based retail application designed for **ElevenSeven Enterprise**'s GoGoQuery Store. The application addresses operational challenges by providing functionalities for **Shoppers** and **Managers**. Shoppers can browse products, manage shopping carts, and make purchases, while Managers can manage inventory and fulfill orders. The application integrates a **MySQL database** for data storage and ensures a streamlined experience for all users.

---

## Features Overview

### 1. **Login Page**
- Allows users to log in with their email and password.
- Validates user credentials with the database.
- Displays error messages for:
  - Empty fields.
  - Incorrect credentials.

### 2. **Register Page**
- Enables users to create an account with email, password, date of birth, and gender.
- Validations include:
  - Email format and uniqueness.
  - Password strength and confirmation.
  - Date of Birth (must be 17+ years old).
- Successful registration redirects users to the **Select Role Page**.

### 3. **Select Role Page**
- Users can select their role as either a **Shopper** or **Manager**.
- Role selection is saved in the database, and a success message is displayed.

### 4. **Shopper Navigation Bar**
- Includes options for:
  - Home.
  - Search (filters items based on input).
  - Cart.
  - Log Out.

### 5. **Manager Navigation Bar**
- Expandable menu with options:
  - Add Item.
  - Manage Queue.
  - Log Out.

### 6. **Shopper Home Page**
- Displays:
  - Welcome message with username.
  - Filterable product list (only items with stock > 0).
- Allows users to view product details.

### 7. **Product Detail Page**
- Displays detailed information about a selected product.
- Users can adjust the quantity and add items to the cart.
- Validations ensure:
  - Stock availability.
  - Positive and valid quantities.

### 8. **Cart Page**
- Displays items in the cart with:
  - Quantity adjustment.
  - Grand total calculation.
  - Remove item and checkout options.
- Checkout creates a new transaction with status **In Queue**.

### 9. **Manager Home Page**
- Displays a welcome message with navigation options for managers.

### 10. **Add Item Page**
- Allows managers to add new products to the inventory.
- Includes validations for:
  - Name, description, and price formats.
  - Stock quantity limits.

### 11. **Queue Management Page**
- Displays transactions in a table view.
- Managers can update transaction status from **In Queue** to **Sent**.

---

## Project Setup

### Prerequisites
1. **Software Requirements**:
   - Eclipse 2020.6 R.
   - Java 11.0.18.
   - JavaFX 17.0.7.
   - MySQL Java Connection Library 8.0.24.
   - XAMPP 8.0.7.

2. **File Extensions**:
   - `.java`, `.class` for code.
   - `.sql` for database scripts.

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/username/GoGoQuery.git
   cd GoGoQuery
   ```
2. Set up the database:
   - Import the provided `.sql` file into your MySQL server.
   - Update database connection details in the project configuration.
3. Install dependencies:
   - Ensure the required JavaFX and MySQL libraries are added to the project build path.
4. Run the project:
   - Launch the application from Eclipse.

---

## Database Schema

### ERD Overview
- Tables include:
  - **Users**: Stores user information and roles.
  - **Products**: Manages inventory.
  - **Transactions**: Tracks purchases and their statuses.
  - **Cart**: Temporarily stores shopper selections.

---

## Application Flow

### Shopper
1. Register → Login → Home Page.
2. Browse items, filter by category, and view product details.
3. Add items to cart → Adjust quantities → Checkout.

### Manager
1. Login → Manager Home Page.
2. Add items to inventory.
3. Manage transaction queues and update statuses.

---

## Validation Rules

1. **General**:
   - All input fields are mandatory.
   - Validations are triggered on button presses or Enter key.

2. **Login**:
   - Fields cannot be empty.
   - Credentials must match database records.

3. **Registration**:
   - Email format: Ends with `@gomail.com`, no special characters except `_` or `.`.
   - Password: Must be alphanumeric and confirmed.
   - Date of Birth: Must indicate age > 17.

4. **Cart**:
   - Spinner value ≤ stock quantity.
   - Checkout clears cart and updates stock.

5. **Add Item**:
   - Item name: 5–70 characters.
   - Description: 10–255 characters.
   - Price: $0.50–$900,000.

---

## Documentation

### Files to Submit:
1. **Code Files**:
   - All `.java` and `.class` files.
   - SQL scripts for database setup.

2. **Assets**:
   - Images, audio, or video used in the project.

3. **Documentation**:
   - Explanation of application functionality.
   - Reference links for assets used.

---

## Evaluation Criteria

| Component     | Weight |
|---------------|--------|
| Assignment    | 40%    |
| Project       | 60%    |

### Notes:
- Late submissions will not be graded.
- Use only designated software to ensure compatibility.
- Adherence to guidelines is mandatory.

---

## License

This project is part of **ISYS6197** for the **Odd Semester 2024/2025** and adheres to the course's academic integrity policies. All rights reserved. 

---
