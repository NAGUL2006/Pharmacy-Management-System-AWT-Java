package com.pharmacy;

import com.pharmacy.service.AuthService;
import com.pharmacy.dao.UserDao;
import com.pharmacy.db.Database;
import com.pharmacy.ui.LoginFrame;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Initialize database and ensure schema exists
        Database.init();

        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService(new UserDao());
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}
