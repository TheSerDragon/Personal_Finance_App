package com.example.finance.model;

import java.io.Serializable;

public class Budget implements Serializable {
    private final String category;
    private double limit;

    public Budget(String category, double limit) {
        this.category = category;
        this.limit = limit;
    }

    public String getCategory() { return category; }
    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }
}