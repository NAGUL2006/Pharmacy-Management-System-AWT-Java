# Pharmacy Management System (Simple)

A minimal Java Swing application with Login/Signup and management tabs for Medicines, Customers, and Billing. Uses SQLite. Passwords are hashed with BCrypt.

## Requirements
- Java 17+
- Maven 3.8+

## How to run

```bash
mvn clean package
java -cp target/pharmacy-management-1.0.0.jar;target/lib/* com.pharmacy.App
```

On Linux/macOS, replace `;` with `:` in the classpath.

This creates `pharmacy.db` in the working directory and initializes tables:
- users
- medicines
- customers
- bills
- bill_items

## Notes
- Login accepts either username or email.
- Password min length: 6.
- Usernames and emails are unique.

## App structure (key classes)
- `com.pharmacy.App` – entry point
- `com.pharmacy.db.Database` / `DBConnection` – database access and schema init (SQLite)
- `com.pharmacy.model.*` – `User`, `Medicine`, `Customer`
- `com.pharmacy.dao.*` – `UserDao`, `MedicineDao`, `CustomerDao`
- `com.pharmacy.service.*` – `AuthService`, `BillingService`
- `com.pharmacy.ui.*` – `LoginFrame`, `SignupFrame`, `DashboardFrame`, `MedicinePanel`, `CustomerPanel`, `BillingPanel`

## SQL schema
See `pharmacy_db.sql` for the full schema used by the app (also applied automatically at startup).
