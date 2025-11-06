package com.pharmacy.dao;

import com.pharmacy.db.Database;
import com.pharmacy.model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDao {

    public boolean usernameOrEmailExists(String username, String email) {
        String sql = "SELECT 1 FROM users WHERE username = ? OR email = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(String username, String email, String passwordHash) {
        String sql = "INSERT INTO users(username, email, password_hash) VALUES(?,?,?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByUsernameOrEmail(String login) {
        String sql = "SELECT id, username, email, password_hash, created_at FROM users WHERE username = ? OR email = ? LIMIT 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    u.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
                    return u;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
