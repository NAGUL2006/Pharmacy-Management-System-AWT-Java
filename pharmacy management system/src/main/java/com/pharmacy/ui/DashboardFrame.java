package com.pharmacy.ui;

import com.pharmacy.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private final User user;

    public DashboardFrame(User user) {
        super("Pharmacy - Dashboard");
        this.user = user;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Logged in as: " + user.getUsername());
        welcome.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        top.add(welcome, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Medicines", new MedicinePanel());
        tabs.addTab("Customers", new CustomerPanel());
        tabs.addTab("Billing", new BillingPanel());
        add(tabs, BorderLayout.CENTER);
    }
}
