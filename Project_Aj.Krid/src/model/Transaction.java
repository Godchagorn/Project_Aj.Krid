package model;

import java.util.Date;

public abstract class Transaction {
    protected String description;
    protected double amount;
    protected Date date;
    protected String category;

    public Transaction(String description, double amount, Date date, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public abstract String getType();

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }
}
