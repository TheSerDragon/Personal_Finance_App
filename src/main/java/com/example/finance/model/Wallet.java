package com.example.finance.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class Wallet implements Serializable {
    private final String owner;
    private final List<Transaction> operations = new ArrayList<>();
    private final Set<String> categories = new HashSet<>();
    private final Map<String, Double> budgets = new HashMap<>();

    public Wallet(String owner) {
        this.owner = owner;
    }

    public void addOperation(Transaction op) {
        operations.add(op);
        if (op.getCategory() != null && !op.getCategory().isBlank()) categories.add(op.getCategory());
    }

    public List<Transaction> getOperations() { return operations; }
    public Set<String> getCategories() { return categories; }
    public Map<String, Double> getBudgets() { return budgets; }

    public double getTotalByType(String type) {
        return operations.stream().filter(o -> o.getType().equals(type)).mapToDouble(Transaction::getAmount).sum();
    }

    public double getTotalByTypeAndCategory(String type, String cat) {
        return operations.stream().filter(o -> o.getType().equals(type) && o.getCategory().equals(cat)).mapToDouble(Transaction::getAmount).sum();
    }

    public Map<String, Double> getTotalsByTypeAndCategory(String type) {
        Map<String, Double> map = new HashMap<>();
        for (Transaction t : operations) {
            if (!t.getType().equals(type)) continue;
            map.merge(t.getCategory() == null ? "(без категории)" : t.getCategory(), t.getAmount(), Double::sum);

        }
        return map;
    }

    public double getBalance() {
        return getTotalByType("income") - getTotalByType("expense");
    }

    public double getRemainingBudget(String cat) {
        if (!budgets.containsKey(cat)) return Double.NaN;
        double bud = budgets.get(cat);
        double spent = getTotalByTypeAndCategory("expense", cat);
        return bud - spent;
    }

    public void setBudget(String category, double amount) {
        budgets.put(category, amount);
    }

}