package com.pharmacy.ui;

import com.pharmacy.model.User;
import com.pharmacy.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService;

    private JTextField tfLogin;
    private JPasswordField pfPassword;

    public LoginFrame(AuthService authService) {
        super("Pharmacy - Login");
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 240);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblLogin = new JLabel("Username or Email:");
        tfLogin = new JTextField(20);
        JLabel lblPassword = new JLabel("Password:");
        pfPassword = new JPasswordField(20);

        JButton btnLogin = new JButton("Login");
        JButton btnSignup = new JButton("Sign Up");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblLogin, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(tfLogin, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(pfPassword, gbc);

        JPanel actions = new JPanel();
        actions.add(btnLogin);
        actions.add(btnSignup);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(actions, gbc);

        setContentPane(panel);

        btnLogin.addActionListener(e -> onLogin());
        btnSignup.addActionListener(e -> onSignup());
    }

    private void onLogin() {
        String login = tfLogin.getText().trim();
        String password = new String(pfPassword.getPassword());
        try {
            User u = authService.login(login, password);
            if (u != null) {
                JOptionPane.showMessageDialog(this, "Welcome, " + u.getUsername() + "!");
                new DashboardFrame(u).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSignup() {
        SignupFrame sf = new SignupFrame(this, authService);
        sf.setLocationRelativeTo(this);
        sf.setVisible(true);
    }
}
