package com.pharmacy.ui;

import com.pharmacy.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JDialog {
    private final AuthService authService;

    private JTextField tfUsername;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirm;

    public SignupFrame(Frame owner, AuthService authService) {
        super(owner, "Sign Up", true);
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setSize(400, 300);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username:");
        tfUsername = new JTextField(20);
        JLabel lblEmail = new JLabel("Email:");
        tfEmail = new JTextField(20);
        JLabel lblPassword = new JLabel("Password:");
        pfPassword = new JPasswordField(20);
        JLabel lblConfirm = new JLabel("Confirm Password:");
        pfConfirm = new JPasswordField(20);

        JButton btnCreate = new JButton("Create Account");

        gbc.gridx=0; gbc.gridy=0; panel.add(lblUsername, gbc);
        gbc.gridx=1; gbc.gridy=0; panel.add(tfUsername, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(lblEmail, gbc);
        gbc.gridx=1; gbc.gridy=1; panel.add(tfEmail, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(lblPassword, gbc);
        gbc.gridx=1; gbc.gridy=2; panel.add(pfPassword, gbc);
        gbc.gridx=0; gbc.gridy=3; panel.add(lblConfirm, gbc);
        gbc.gridx=1; gbc.gridy=3; panel.add(pfConfirm, gbc);
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; panel.add(btnCreate, gbc);

        setContentPane(panel);

        btnCreate.addActionListener(e -> onCreate());
    }

    private void onCreate() {
        String username = tfUsername.getText().trim();
        String email = tfEmail.getText().trim();
        String password = new String(pfPassword.getPassword());
        String confirm = new String(pfConfirm.getPassword());
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            authService.signup(username, email, password);
            JOptionPane.showMessageDialog(this, "Account created. You can login now.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
