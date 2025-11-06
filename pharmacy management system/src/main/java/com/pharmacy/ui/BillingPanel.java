package com.pharmacy.ui;

import com.pharmacy.dao.CustomerDao;
import com.pharmacy.dao.MedicineDao;
import com.pharmacy.model.Customer;
import com.pharmacy.model.Medicine;
import com.pharmacy.service.BillingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BillingPanel extends JPanel {
    private final CustomerDao customerDao = new CustomerDao();
    private final MedicineDao medicineDao = new MedicineDao();
    private final BillingService billingService = new BillingService();

    private JComboBox<CustomerItem> cbCustomer;
    private JTable tblMedicines, tblCart;
    private DefaultTableModel modelMedicines, modelCart;
    private JTextField tfFilter, tfQuantity;
    private JLabel lblTotal;

    public BillingPanel() {
        setLayout(new BorderLayout(8,8));
        add(initTop(), BorderLayout.NORTH);
        add(initCenter(), BorderLayout.CENTER);
        add(initBottom(), BorderLayout.SOUTH);
        reloadCustomers();
        reloadMedicines("");
        updateTotal();
    }

    private JPanel initTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbCustomer = new JComboBox<>();
        JButton btnReloadCust = new JButton("Reload Customers");
        p.add(new JLabel("Customer:"));
        p.add(cbCustomer);
        p.add(btnReloadCust);
        btnReloadCust.addActionListener(e -> reloadCustomers());
        return p;
    }

    private JComponent initCenter() {
        // Left: medicines with search and quantity to add
        JPanel left = new JPanel(new BorderLayout(4,4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfFilter = new JTextField(15);
        JButton btnSearch = new JButton("Search");
        search.add(new JLabel("Find:"));
        search.add(tfFilter);
        search.add(btnSearch);
        left.add(search, BorderLayout.NORTH);

        modelMedicines = new DefaultTableModel(new Object[]{"ID","Name","Company","Qty","Price"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMedicines = new JTable(modelMedicines);
        left.add(new JScrollPane(tblMedicines), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfQuantity = new JTextField(5);
        JButton btnAdd = new JButton("Add Item");
        addPanel.add(new JLabel("Qty:"));
        addPanel.add(tfQuantity);
        addPanel.add(btnAdd);
        left.add(addPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> reloadMedicines(tfFilter.getText().trim()));
        btnAdd.addActionListener(e -> addSelectedMedicine());

        // Right: cart
        modelCart = new DefaultTableModel(new Object[]{"MedID","Name","Qty","Price","Line Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(modelCart);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, new JScrollPane(tblCart));
        split.setResizeWeight(0.55);
        return split;
    }

    private JPanel initBottom() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRemove = new JButton("Remove Selected");
        JButton btnClear = new JButton("Clear Cart");
        left.add(btnRemove);
        left.add(btnClear);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: 0.00");
        JButton btnCreate = new JButton("Create Bill");
        right.add(lblTotal);
        right.add(btnCreate);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);

        btnRemove.addActionListener(e -> removeSelectedCart());
        btnClear.addActionListener(e -> clearCart());
        btnCreate.addActionListener(e -> createBill());
        return p;
    }

    private void reloadCustomers() {
        cbCustomer.removeAllItems();
        for (Customer c : customerDao.listAll()) {
            cbCustomer.addItem(new CustomerItem(c.getId(), c.getName()));
        }
    }

    private void reloadMedicines(String q) {
        modelMedicines.setRowCount(0);
        List<Medicine> meds = (q == null || q.isEmpty()) ? medicineDao.listAll() : medicineDao.searchByName(q);
        for (Medicine m : meds) {
            modelMedicines.addRow(new Object[]{ m.getId(), m.getName(), m.getCompany(), m.getQuantity(), m.getPrice() });
        }
    }

    private void addSelectedMedicine() {
        int r = tblMedicines.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a medicine"); return; }
        int medId = (int) modelMedicines.getValueAt(r, 0);
        String name = String.valueOf(modelMedicines.getValueAt(r, 1));
        int stock = (int) modelMedicines.getValueAt(r, 3);
        double price = (double) modelMedicines.getValueAt(r, 4);
        int qty;
        try { qty = Integer.parseInt(tfQuantity.getText().trim()); } catch (Exception ex) { qty = 1; }
        if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be > 0"); return; }
        if (qty > stock) { JOptionPane.showMessageDialog(this, "Insufficient stock"); return; }
        double line = qty * price;
        modelCart.addRow(new Object[]{ medId, name, qty, price, line });
        updateTotal();
        tfQuantity.setText("");
    }

    private void removeSelectedCart() {
        int r = tblCart.getSelectedRow();
        if (r >= 0) {
            modelCart.removeRow(r);
            updateTotal();
        }
    }

    private void clearCart() {
        modelCart.setRowCount(0);
        updateTotal();
    }

    private void updateTotal() {
        double total = 0.0;
        for (int i = 0; i < modelCart.getRowCount(); i++) {
            total += ((Number) modelCart.getValueAt(i, 4)).doubleValue();
        }
        lblTotal.setText(String.format("Total: %.2f", total));
    }

    private void createBill() {
        if (modelCart.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Cart is empty"); return; }
        List<BillingService.Item> items = new ArrayList<>();
        for (int i = 0; i < modelCart.getRowCount(); i++) {
            BillingService.Item it = new BillingService.Item();
            it.medId = (int) modelCart.getValueAt(i, 0);
            it.quantity = (int) modelCart.getValueAt(i, 2);
            it.price = ((Number) modelCart.getValueAt(i, 3)).doubleValue();
            items.add(it);
        }
        CustomerItem sel = (CustomerItem) cbCustomer.getSelectedItem();
        Integer custId = sel != null ? sel.id : null;
        try {
            int billId = billingService.createBill(custId, items);
            JOptionPane.showMessageDialog(this, "Bill #"+billId+" created.");
            clearCart();
            reloadMedicines(tfFilter.getText().trim()); // refresh stock
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class CustomerItem {
        final Integer id; final String name;
        CustomerItem(Integer id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name + (id != null ? " (#"+id+")" : ""); }
    }
}
