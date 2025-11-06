package com.pharmacy.ui;

import com.pharmacy.dao.MedicineDao;
import com.pharmacy.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicinePanel extends JPanel {
    private final MedicineDao dao = new MedicineDao();

    private JTable table;
    private DefaultTableModel model;
    private JTextField tfName, tfCompany, tfQuantity, tfPrice, tfSearch;

    public MedicinePanel() {
        setLayout(new BorderLayout());
        initTopBar();
        initTable();
        initForm();
        reload();
    }

    private void initTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        JButton btnRefresh = new JButton("Refresh");
        top.add(new JLabel("Search:"));
        top.add(tfSearch);
        top.add(btnSearch);
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> reload(tfSearch.getText().trim()));
        btnRefresh.addActionListener(e -> reload());
    }

    private void initTable() {
        model = new DefaultTableModel(new Object[]{"ID","Name","Company","Qty","Price"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                tfName.setText(String.valueOf(model.getValueAt(r,1)));
                tfCompany.setText(String.valueOf(model.getValueAt(r,2)));
                tfQuantity.setText(String.valueOf(model.getValueAt(r,3)));
                tfPrice.setText(String.valueOf(model.getValueAt(r,4)));
            }
        });
    }

    private void initForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfName = new JTextField(15);
        tfCompany = new JTextField(15);
        tfQuantity = new JTextField(8);
        tfPrice = new JTextField(8);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        int y=0;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Name"), gbc);
        gbc.gridx=1; form.add(tfName, gbc);
        gbc.gridx=2; form.add(new JLabel("Company"), gbc);
        gbc.gridx=3; form.add(tfCompany, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Qty"), gbc);
        gbc.gridx=1; form.add(tfQuantity, gbc);
        gbc.gridx=2; form.add(new JLabel("Price"), gbc);
        gbc.gridx=3; form.add(tfPrice, gbc);
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
            Medicine m = readForm(null);
            dao.add(m);
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
            Medicine m = readForm(id);
            dao.update(m);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        int id = (int) model.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete medicine #"+id+"?");
        if (confirm == JOptionPane.YES_OPTION) {
            dao.delete(id);
            reload();
        }
    }

    private Medicine readForm(Integer id) {
        String name = tfName.getText().trim();
        String comp = tfCompany.getText().trim();
        int qty = Integer.parseInt(tfQuantity.getText().trim());
        double price = Double.parseDouble(tfPrice.getText().trim());
        Medicine m = new Medicine(id, name, comp, qty, price);
        return m;
    }

    private void clearForm() {
        tfName.setText("");
        tfCompany.setText("");
        tfQuantity.setText("");
        tfPrice.setText("");
    }

    private void reload() { reload(""); }
    private void reload(String q) {
        List<Medicine> list = (q == null || q.isEmpty()) ? dao.listAll() : dao.searchByName(q);
        model.setRowCount(0);
        for (Medicine m : list) {
            model.addRow(new Object[]{ m.getId(), m.getName(), m.getCompany(), m.getQuantity(), m.getPrice() });
        }
    }
}
