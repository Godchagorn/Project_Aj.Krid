package model;

import java.util.Date;

public class Expense extends Transaction {
    public Expense(String description, double amount, Date date, String category) {
        super(description, amount, date, category);
    }

    @Override
    public String getType() {
        return "Expense";
    }
}
