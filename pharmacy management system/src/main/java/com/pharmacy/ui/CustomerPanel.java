package com.pharmacy.ui;

import com.pharmacy.dao.CustomerDao;
import com.pharmacy.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private final CustomerDao dao = new CustomerDao();

    private JTable table;
    private DefaultTableModel model;
    private JTextField tfName, tfPhone;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        initTable();
        initForm();
        reload();
    }

    private void initTable() {
        model = new DefaultTableModel(new Object[]{"ID","Name","Phone"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                tfName.setText(String.valueOf(model.getValueAt(r,1)));
                tfPhone.setText(String.valueOf(model.getValueAt(r,2)));
            }
        });
    }

    private void initForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfName = new JTextField(15);
        tfPhone = new JTextField(12);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        int y=0;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Name"), gbc);
        gbc.gridx=1; form.add(tfName, gbc);
        gbc.gridx=2; form.add(new JLabel("Phone"), gbc);
        gbc.gridx=3; form.add(tfPhone, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=4;
        JPanel actions = new JPanel();
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        form.add(actions, gbc);

        add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
    }

    private void onAdd() {
        try {
            Customer c = new Customer(null, tfName.getText().trim(), tfPhone.getText().trim());
            dao.add(c);
            clearForm();
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        try {
            int id = (int) model.getValueAt(r, 0);
            Customer c = new Customer(id, tfName.getText().trim(), tfPhone.getText().trim());
            dao.update(c);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        int id = (int) model.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete customer #"+id+"?");
        if (confirm == JOptionPane.YES_OPTION) {
            dao.delete(id);
            reload();
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfPhone.setText("");
    }

    private void reload() {
        List<Customer> list = dao.listAll();
        model.setRowCount(0);
        for (Customer c : list) {
            model.addRow(new Object[]{ c.getId(), c.getName(), c.getPhone() });
        }
    }
}
