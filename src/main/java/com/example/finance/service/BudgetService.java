package com.example.finance.service;

import com.example.finance.model.User;
import com.example.finance.model.Wallet;

public class BudgetService {
    public void addCategory(User user, String name) {
        if (name == null || name.isBlank()) { System.out.println("Имя категории не может быть пустым."); return; }
        user.getWallet().getCategories().add(name);
        System.out.println("Категория добавлена: " + name);
    }

    public void setBudget(User user, String arg) {
        if (arg == null || arg.isBlank()) { System.out.println("Использование: set_budget <category>:<amount>"); return; }
        String[] parts = arg.split(":",2);
        if (parts.length < 2) { System.out.println("Использование: set_budget <category>:<amount>"); return; }
        String cat = parts[0].trim();
        double amt;
        try { amt = Double.parseDouble(parts[1].trim()); } catch (NumberFormatException e) { System.out.println("Неверная сумма."); return; }
        if (amt < 0) { System.out.println("Бюджет не может быть отрицательным."); return; }
        Wallet w = user.getWallet();
        w.getBudgets().put(cat, amt);
        w.getCategories().add(cat);
        System.out.println("Бюджет установлен: " + cat + " = " + String.format("%,.2f", amt));
    }
}