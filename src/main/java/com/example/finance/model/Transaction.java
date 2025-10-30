package com.example.finance.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transaction implements Serializable {
    private final String type;
    private final String category;
    private final double amount;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(String type, String category, double amount, String description) {
        this.type = type;
        this.category = category == null || category.isBlank() ? "(без категории)" : category;
        this.amount = amount;
        this.description = description == null ? "" : description;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s | %s | %s | %s", timestamp.format(f), type, category, String.format(Locale.forLanguageTag("ru-RU"), "%,.2f", amount), description);
    }
}
