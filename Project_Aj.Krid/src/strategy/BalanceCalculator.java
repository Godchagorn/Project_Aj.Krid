package strategy;

import model.Transaction;
import java.util.List;

public interface BalanceCalculator {
    double calculateBalance(List<Transaction> transactions);
}
