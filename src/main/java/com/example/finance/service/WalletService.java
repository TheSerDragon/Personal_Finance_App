package com.example.finance.service;

import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import com.example.finance.model.Wallet;
import com.example.finance.storage.Storage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WalletService {
    private final Storage storage;

    // Внедрение зависимости через конструктор
    public WalletService(Storage storage) {
        this.storage = storage;
    }

    public void loadWallet(User user) {
        Wallet w = storage.loadWallet(user.getName());
        if (w != null) {
            user.getWallet().getOperations().addAll(w.getOperations());
            user.getWallet().getBudgets().putAll(w.getBudgets());
            user.getWallet().getCategories().addAll(w.getCategories());
        }
    }

    public void saveWallet(User user) {
        storage.saveWallet(user.getName(), user.getWallet());
        System.out.println("Кошелёк сохранён.");
    }

private static class ParsedOp {
        boolean valid = false;
        String category;
        double amount;
        String desc;
    }

    public void addIncome(User user, String arg) {
        ParsedOp p = parseOp(arg);
        if (!p.valid) return;
        user.getWallet().addOperation(new Transaction("income", p.category, p.amount, p.desc));
        System.out.println("Доход добавлен.");
        checkAlerts(user);
    }

    public void addExpense(User user, String arg, Scanner scanner) {
        ParsedOp p = parseOp(arg);
        if (!p.valid) return;
        Wallet w = user.getWallet();
        if (!w.getCategories().contains(p.category)) {
            System.out.println("Категория '" + p.category + "' не найдена. Создать автоматически? (y/n)");
            String r = scanner.nextLine().trim().toLowerCase();
            if (r.startsWith("y")) {
                w.getCategories().add(p.category);
                System.out.println("Категория создана: " + p.category);
            } else {
                System.out.println("Операция отменена.");
                return;
            }
        }
        w.addOperation(new Transaction("expense", p.category, p.amount, p.desc));
        System.out.println("Расход добавлен.");
        double rem = w.getRemainingBudget(p.category);
        if (!Double.isNaN(rem) && rem < 0) {
            System.out.println("ВНИМАНИЕ: бюджет по категории '" + p.category + "' превышен на " + String.format("%,.2f", -rem));
        }
        checkAlerts(user);
    }

    private ParsedOp parseOp(String arg) {
        ParsedOp p = new ParsedOp();
        if (arg == null || arg.isBlank()) {
            System.out.println("Неверный формат. Ожидается: <category>:<amount>:<description>");
            return p;
        }
        String[] parts = arg.split(":", 3);
        if (parts.length < 2) {
            System.out.println("Неверный формат. Ожидается: <category>:<amount>:<description>");
            return p;
        }
        p.category = parts[0].trim();
        try {
            p.amount = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("Неверная сумма.");
            return p;
        }
        if (p.amount <= 0) {
            System.out.println("Сумма должна быть положительной.");
            return p;
        }
        p.desc = parts.length > 2 ? parts[2].trim() : "";
        p.valid = true;
        return p;
    }

    public void printSummary(User user) {
        Wallet w = user.getWallet();
        System.out.println("----- Сводка для пользователя: " + user.getName() + " -----");
        System.out.println("Общий доход: " + String.format("%,.2f", w.getTotalByType("income")));
        System.out.println("Доходы по категориям:");
        Map<String, Double> inc = w.getTotalsByTypeAndCategory("income");
        if (inc.isEmpty()) System.out.println("  (нет)"); else inc.forEach((c,v)->System.out.println("  "+c+": "+String.format("%,.2f", v)));
        System.out.println("Общие расходы: " + String.format("%,.2f", w.getTotalByType("expense")));
        System.out.println("Расходы по категориям:");
        Map<String, Double> exp = w.getTotalsByTypeAndCategory("expense");
        if (exp.isEmpty()) System.out.println("  (нет)"); else exp.forEach((c,v)->System.out.println("  "+c+": "+String.format("%,.2f", v)));
        System.out.println("Бюджет по категориям:");
        if (w.getBudgets().isEmpty()) System.out.println("  (нет)"); else {
            w.getBudgets().forEach((cat,bud)-> {
                double rem = w.getRemainingBudget(cat);
                System.out.println("  "+cat+": "+String.format("%,.2f", bud)+", Оставшийся бюджет: "+String.format("%,.2f", rem));
            });
        }
        System.out.println("Баланс кошелька: " + String.format("%,.2f", w.getBalance()));
        if (w.getTotalByType("expense") > w.getTotalByType("income")) {
            System.out.println("ВНИМАНИЕ: Совокупные расходы превышают доходы!");
        }
    }

    public void listOperations(User user, int n) {
        List<Transaction> ops = user.getWallet().getOperations();
        if (ops.isEmpty()) { System.out.println("Операций нет."); return; }
        int start = Math.max(0, ops.size() - n);
        for (int i = ops.size() - 1; i >= start; i--) {
            System.out.println(ops.get(i));
        }
    }

    public void totalsByCategories(User user, String arg) {
        if (arg == null || arg.isBlank()) {
            System.out.println("Общие суммы по всем категориям:");
            Map<String, Double> totals = user.getWallet().getTotalsByTypeAndCategory("expense");
            if (totals.isEmpty()) {
                System.out.println("  (нет)");
            } else {
                totals.forEach((c, v) -> System.out.println("  " + c + ": " + String.format("%,.2f", v)));
            }
            return;
        }
        String[] cats = arg.split(",");
        double sumInc=0, sumExp=0;
        for (String raw : cats) {
            String cat = raw.trim();
            if (cat.isEmpty()) continue;
            if (!user.getWallet().getCategories().contains(cat) && !user.getWallet().getBudgets().containsKey(cat)) {
                System.out.println("Категория не найдена: " + cat);
                continue;
            }
            sumInc += user.getWallet().getTotalByTypeAndCategory("income", cat);
            sumExp += user.getWallet().getTotalByTypeAndCategory("expense", cat);
        }
        System.out.println("Сумма доходов по выбранным категориям: " + String.format("%,.2f", sumInc));
        System.out.println("Сумма расходов по выбранным категориям: " + String.format("%,.2f", sumExp));
    }

    private void checkAlerts(User user) {
        Wallet w = user.getWallet();
        if (w.getTotalByType("expense") > w.getTotalByType("income")) {
            System.out.println("ВНИМАНИЕ: Общие расходы превысили доходы!");
        }
        for (String cat : w.getBudgets().keySet()) {
            double rem = w.getRemainingBudget(cat);
            if (rem < 0) {
                System.out.println(String.format("ВНИМАНИЕ: Бюджет по категории '%s' превышен на %s", cat, String.format("%,.2f", -rem)));
            }
        }
    }

    public void exportToJson(User user) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class,
                            (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                                    new com.google.gson.JsonPrimitive(src.format(formatter)))
                    .registerTypeAdapter(LocalDateTime.class,
                            (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                                    LocalDateTime.parse(json.getAsString(), formatter))
                    .setPrettyPrinting()
                    .create();

            String fileName = "export_" + user.getName() + ".json";
            try (FileWriter writer = new FileWriter(fileName)) {
                gson.toJson(user.getWallet(), writer);
            }
            System.out.println("Данные экспортированы в файл: " + fileName);
        } catch (Exception e) {
            System.out.println("Ошибка экспорта: " + e.getMessage());
        }
    }
}
