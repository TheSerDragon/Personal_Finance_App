package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.WalletService;
import com.example.finance.service.UserService;
import com.example.finance.storage.FileStorage;
import com.example.finance.storage.Storage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class WalletServiceTest {

    @Test
    public void testAddIncomeAndExpenseBalance() {
        // Создаём сервисы
        UserService userService = new UserService();
        userService.register("testuser", "pass1234");
        User user = userService.login("testuser", "pass1234");

        // Передаём FileStorage
        Storage storage = new FileStorage();
        WalletService walletService = new WalletService(storage);

        // Добавляем доход
        walletService.addIncome(user, "Зарплата:1000:ok");

        // Симулируем ввод "y" для подтверждения создания категории
        String simulatedInput = "y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Добавляем расход
        walletService.addExpense(user, "Еда:200:lunch", scanner);

        // Проверяем баланс
        assertEquals(800.0, user.getWallet().getBalance(), 0.0001);
    }
}
