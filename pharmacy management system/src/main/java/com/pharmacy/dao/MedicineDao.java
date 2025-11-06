package com.pharmacy.dao;

import com.pharmacy.db.Database;
import com.pharmacy.model.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDao {
    public void add(Medicine m) {
        String sql = "INSERT INTO medicines(med_name, company, quantity, price) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCompany());
            ps.setInt(3, m.getQuantity());
            ps.setDouble(4, m.getPrice());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(Medicine m) {
        String sql = "UPDATE medicines SET med_name=?, company=?, quantity=?, price=? WHERE med_id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCompany());
            ps.setInt(3, m.getQuantity());
            ps.setDouble(4, m.getPrice());
            ps.setInt(5, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM medicines WHERE med_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Medicine> listAll() {
        List<Medicine> list = new ArrayList<>();
        try (Connection c = Database.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT med_id, med_name, company, quantity, price FROM medicines ORDER BY med_name")) {
            while (rs.next()) {
                list.add(new Medicine(
                        rs.getInt("med_id"),
                        rs.getString("med_name"),
                        rs.getString("company"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<Medicine> searchByName(String q) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT med_id, med_name, company, quantity, price FROM medicines WHERE med_name LIKE ? ORDER BY med_name";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Medicine(
                            rs.getInt("med_id"),
                            rs.getString("med_name"),
                            rs.getString("company"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
