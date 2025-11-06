package com.pharmacy.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    // Facade to match requested naming while reusing Database
    public static Connection getConnection() throws SQLException {
        return Database.getConnection();
    }
}
