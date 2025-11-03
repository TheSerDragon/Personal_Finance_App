package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.*;
import com.example.finance.storage.FileStorage;
import com.example.finance.storage.Storage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class SummaryCalculationTest {

    @Test
    public void testCategoryTotals() {
        // Создаём пользователя
        UserService userService = new UserService();
        userService.register("summaryUser", "1234");
        User user = userService.login("summaryUser", "1234");

        // Передаём FileStorage
        Storage storage = new FileStorage();
        WalletService walletService = new WalletService(storage);

        // Добавляем доход
        walletService.addIncome(user, "Зарплата:1000:test");

        // Симулируем подтверждение "y" для категории
        String simulatedInput = "y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Добавляем расходы
        walletService.addExpense(user, "Еда:200:lunch", scanner);

        // Второй расход — тоже с симуляцией
        scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        walletService.addExpense(user, "Еда:100:coffee", scanner);

        // Проверки
        double totalExp = user.getWallet().getTotalByTypeAndCategory("expense", "Еда");
        double totalInc = user.getWallet().getTotalByTypeAndCategory("income", "Зарплата");

        assertEquals(300.0, totalExp, 0.01);
        assertEquals(1000.0, totalInc, 0.01);
        assertEquals(700.0, user.getWallet().getBalance(), 0.01);
    }
}
