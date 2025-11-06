package com.pharmacy.service;

import com.pharmacy.db.Database;

import java.sql.*;
import java.util.List;

public class BillingService {
    public static class Item { public int medId; public int quantity; public double price; }

    public int createBill(Integer customerId, List<Item> items) {
        String insertBill = "INSERT INTO bills(cust_id, total) VALUES(?,?)";
        String insertItem = "INSERT INTO bill_items(bill_id, med_id, quantity, price) VALUES(?,?,?,?)";
        String updateStock = "UPDATE medicines SET quantity = quantity - ? WHERE med_id = ? AND quantity >= ?";
        double total = items.stream().mapToDouble(i -> i.price * i.quantity).sum();
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psBill = conn.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psItem = conn.prepareStatement(insertItem);
                 PreparedStatement psStock = conn.prepareStatement(updateStock)) {
                if (customerId == null) psBill.setNull(1, Types.INTEGER); else psBill.setInt(1, customerId);
                psBill.setDouble(2, total);
                psBill.executeUpdate();
                int billId;
                try (ResultSet keys = psBill.getGeneratedKeys()) {
                    keys.next();
                    billId = keys.getInt(1);
                }
                for (Item it : items) {
                    // reduce stock
                    psStock.setInt(1, it.quantity);
                    psStock.setInt(2, it.medId);
                    psStock.setInt(3, it.quantity);
                    int updated = psStock.executeUpdate();
                    if (updated == 0) throw new SQLException("Insufficient stock for med_id=" + it.medId);
                    // add item
                    psItem.setInt(1, billId);
                    psItem.setInt(2, it.medId);
                    psItem.setInt(3, it.quantity);
                    psItem.setDouble(4, it.price);
                    psItem.executeUpdate();
                }
                conn.commit();
                return billId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
