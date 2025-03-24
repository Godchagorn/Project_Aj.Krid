package strategy;

import model.Transaction;
import model.Income;
import model.Expense;

import java.util.List;

public class DefaultBalanceCalculator implements BalanceCalculator {
    @Override
    public double calculateBalance(List<Transaction> transactions) {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t instanceof Income) {
                balance += t.getAmount();
            } else if (t instanceof Expense) {
                balance -= t.getAmount();
            }
        }
        return balance;
    }
}
