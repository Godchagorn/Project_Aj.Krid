package ui;

import model.Transaction;
import factory.TransactionFactory;
import observer.TransactionManager;
import strategy.DefaultBalanceCalculator;
import observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class ExpenseTrackerGUI extends JFrame implements Observer {
    private TransactionManager manager;
    private JLabel balanceLabel;
    private DefaultListModel<String> listModel;
    private JList<String> transactionList;
    private JTextField searchField;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ExpenseTrackerGUI() {
        manager = new TransactionManager(new DefaultBalanceCalculator());
        manager.addObserver(this);

        setTitle("Expense Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        balanceLabel = new JLabel("💰 Balance: 0.00 Baht", JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balancePanel.add(balanceLabel);
        topPanel.add(balancePanel);

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchTransactions());
        searchPanel.add(new JLabel("Search for transactions:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton dailySummary = new JButton("Daily Summary");
        JButton monthlySummary = new JButton("Monthly Summary");
        JButton yearlySummary = new JButton("Yearly Summary");

        dailySummary.addActionListener(e -> showSummary("Daily", manager.getDailySummary()));
        monthlySummary.addActionListener(e -> showSummary("Monthly", manager.getMonthlySummary()));
        yearlySummary.addActionListener(e -> showSummary("Yearly", manager.getYearlySummary()));

        summaryPanel.add(dailySummary);
        summaryPanel.add(monthlySummary);
        summaryPanel.add(yearlySummary);
        topPanel.add(summaryPanel);

        add(topPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        transactionList = new JList<>(listModel);
        add(new JScrollPane(transactionList), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        JTextField descField = new JTextField(10);
        JTextField amountField = new JTextField(5);
        JComboBox<String> categoryBox = new JComboBox<>(model.Category.CATEGORIES);
        JButton addIncome = new JButton("Income");
        JButton addExpense = new JButton("Expense");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(categoryBox);
        inputPanel.add(addIncome);
        inputPanel.add(addExpense);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);
        add(inputPanel, BorderLayout.SOUTH);

        addIncome.addActionListener(e -> addTransaction("Income", descField.getText(), amountField.getText(), categoryBox.getSelectedItem().toString()));
        addExpense.addActionListener(e -> addTransaction("Expense", descField.getText(), amountField.getText(), categoryBox.getSelectedItem().toString()));

        deleteButton.addActionListener(e -> deleteTransaction());
        editButton.addActionListener(e -> editTransaction(descField, amountField, categoryBox));

        update();
    }

    private void showSummary(String type, Map<String, Double> summaryData) {
        String[] columnNames = {"Date", "Total"};
        Object[][] data = new Object[summaryData.size()][2];

        int i = 0;
        for (Map.Entry<String, Double> entry : summaryData.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            i++;
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JOptionPane.showMessageDialog(this, scrollPane, type + " Summary", JOptionPane.INFORMATION_MESSAGE);
    }


    private void addTransaction(String type, String description, String amount, String category) {
        if (description.trim().isEmpty() || amount.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double amt = Double.parseDouble(amount);
            manager.addTransaction(TransactionFactory.createTransaction(type, description, amt, new Date(), category));
            searchField.setText("");
            update();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaction() {
        if (transactionList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
            return;
        }
        int selectedIndex = transactionList.getSelectedIndex();
        manager.removeTransaction(selectedIndex);
        searchField.setText("");
        update();
    }

    private void editTransaction(JTextField descField, JTextField amountField, JComboBox<String> categoryBox) {
        if (transactionList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.");
            return;
        }
        int selectedIndex = transactionList.getSelectedIndex();
        String description = descField.getText();
        String amountStr = amountField.getText();
        String category = (String) categoryBox.getSelectedItem();

        try {
            double amount = Double.parseDouble(amountStr);
            Transaction oldTransaction = manager.getTransactions().get(selectedIndex);
            Transaction updatedTransaction = TransactionFactory.createTransaction(oldTransaction.getType(), description, amount, oldTransaction.getDate(), category);
            manager.editTransaction(selectedIndex, updatedTransaction);
            searchField.setText("");
            update();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
        }
    }

    private void searchTransactions() {
        String keyword = searchField.getText().trim();
        List<Transaction> filteredTransactions = manager.searchTransactions(keyword);
        listModel.clear();
        for (Transaction t : filteredTransactions) {
            listModel.addElement(dateFormat.format(t.getDate()) + " | " + t.getType() + ": " + t.getDescription() + " (" + t.getAmount() + " Baht)");
        }
    }

    @Override
    public void update() {
        if (listModel == null || balanceLabel == null) return;

        balanceLabel.setText("Balance: " + manager.getBalance() + " Baht");

        listModel.clear();
        for (Transaction t : manager.getTransactions()) {
            listModel.addElement(dateFormat.format(t.getDate()) + " | " + t.getType() + ": " + t.getDescription() + " (" + t.getAmount() + " Baht)");
        }
    }
}
