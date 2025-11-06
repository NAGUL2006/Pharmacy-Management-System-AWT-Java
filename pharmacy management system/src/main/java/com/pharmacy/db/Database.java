package com.pharmacy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:pharmacy.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password_hash TEXT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(sql);

            // Medicines
            String medicines = "CREATE TABLE IF NOT EXISTS medicines (" +
                    "med_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "med_name TEXT NOT NULL, " +
                    "company TEXT, " +
                    "quantity INTEGER NOT NULL DEFAULT 0, " +
                    "price REAL NOT NULL DEFAULT 0" +
                    ")";
            stmt.execute(medicines);

            // Customers
            String customers = "CREATE TABLE IF NOT EXISTS customers (" +
                    "cust_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cust_name TEXT NOT NULL, " +
                    "phone TEXT" +
                    ")";
            stmt.execute(customers);

            // Bills
            String bills = "CREATE TABLE IF NOT EXISTS bills (" +
                    "bill_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cust_id INTEGER, " +
                    "total REAL NOT NULL, " +
                    "bill_date TEXT DEFAULT (datetime('now')), " +
                    "FOREIGN KEY(cust_id) REFERENCES customers(cust_id)" +
                    ")";
            stmt.execute(bills);

            // Bill items
            String billItems = "CREATE TABLE IF NOT EXISTS bill_items (" +
                    "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bill_id INTEGER NOT NULL, " +
                    "med_id INTEGER NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "FOREIGN KEY(bill_id) REFERENCES bills(bill_id), " +
                    "FOREIGN KEY(med_id) REFERENCES medicines(med_id)" +
                    ")";
            stmt.execute(billItems);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
