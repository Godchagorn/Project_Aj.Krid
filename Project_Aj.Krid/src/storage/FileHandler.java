package storage;

import model.Transaction;
import model.Income;
import model.Expense;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileHandler {
    private static final String FILE_NAME = "transactions.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Transaction t : transactions) {
                writer.write(t.getType() + "," + t.getDescription() + "," + t.getAmount() + "," +
                        DATE_FORMAT.format(t.getDate()) + "," + t.getCategory());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0];
                String description = data[1];
                double amount = Double.parseDouble(data[2]);
                Date date = DATE_FORMAT.parse(data[3]);
                String category = data[4];

                if (type.equals("Income")) {
                    transactions.add(new Income(description, amount, date, category));
                } else if (type.equals("Expense")) {
                    transactions.add(new Expense(description, amount, date, category));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
