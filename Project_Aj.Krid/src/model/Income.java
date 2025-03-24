package model;

import java.util.Date;

public class Income extends Transaction {
    public Income(String description, double amount, Date date, String category) {
        super(description, amount, date, category);
    }

    @Override
    public String getType() {
        return "Income";
    }
}
