package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.*;
import com.example.finance.storage.FileStorage;
import com.example.finance.storage.Storage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetLimitAlertTest {

    @Test
    public void testBudgetLimitExceeded() {
        // Создаём пользователя
        UserService userService = new UserService();
        userService.register("testBudget", "1234");
        User user = userService.login("testBudget", "1234");

        // Передаём FileStorage
        Storage storage = new FileStorage();
        WalletService walletService = new WalletService(storage);
        BudgetService budgetService = new BudgetService();

        // Настраиваем категорию и бюджет
        budgetService.addCategory(user, "Еда");
        budgetService.setBudget(user, "Еда:500");

        // Симулируем ввод "y" для подтверждения создания категории
        String simulatedInput = "y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Добавляем расход, превышающий бюджет
        walletService.addExpense(user, "Еда:700:ужин", scanner);

        double remaining = user.getWallet().getRemainingBudget("Еда");
        assertTrue(remaining < 0, "Бюджет должен быть превышен");
    }
}
