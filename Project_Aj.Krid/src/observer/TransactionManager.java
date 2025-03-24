package observer;

import model.Expense;
import model.Income;
import model.Transaction;
import strategy.BalanceCalculator;
import storage.FileHandler;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {
    private List<Transaction> transactions;
    private List<Observer> observers = new ArrayList<>();
    private BalanceCalculator balanceCalculator;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public TransactionManager(BalanceCalculator balanceCalculator) {
        this.balanceCalculator = balanceCalculator;
        this.transactions = FileHandler.loadTransactions();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        saveAndUpdate();
    }

    public void removeTransaction(int index) {
        if (index >= 0 && index < transactions.size()) {
            transactions.remove(index);
            saveAndUpdate();
        }
    }

    public void editTransaction(int index, Transaction updatedTransaction) {
        if (index >= 0 && index < transactions.size()) {
            transactions.set(index, updatedTransaction);
            saveAndUpdate();
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return transactions;
        }

        keyword = keyword.toLowerCase();

        String finalKeyword = keyword;
        return transactions.stream()
                .filter(t -> Objects.requireNonNullElse(t.getDescription(), "").toLowerCase().contains(finalKeyword) ||
                        Objects.requireNonNullElse(t.getCategory(), "").toLowerCase().contains(finalKeyword) ||
                        String.valueOf(t.getAmount()).contains(finalKeyword))
                .collect(Collectors.toList());
    }

    public double getBalance() {
        return balanceCalculator.calculateBalance(transactions);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void saveAndUpdate() {
        FileHandler.saveTransactions(transactions);
        notifyObservers();
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public Map<String, Double> getDailySummary() {
        return summarizeTransactions("yyyy-MM-dd");
    }

    public Map<String, Double> getMonthlySummary() {
        return summarizeTransactions("yyyy-MM");
    }

    public Map<String, Double> getYearlySummary() {
        return summarizeTransactions("yyyy");
    }

    private Map<String, Double> summarizeTransactions(String dateFormatPattern) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatPattern);

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> format.format(t.getDate()),
                        Collectors.summingDouble(t -> t instanceof Income ? t.getAmount() : -t.getAmount())
                ));
    }

}
