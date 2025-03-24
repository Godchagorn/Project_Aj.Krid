package factory;

import model.Transaction;
import model.Income;
import model.Expense;

import java.util.Date;

public class TransactionFactory {
    public static Transaction createTransaction(String type, String description, double amount, Date date, String category) {
        if (type.equalsIgnoreCase("Income")) {
            return new Income(description, amount, date, category);
        } else if (type.equalsIgnoreCase("Expense")) {
            return new Expense(description, amount, date, category);
        }
        return null;
    }
}
