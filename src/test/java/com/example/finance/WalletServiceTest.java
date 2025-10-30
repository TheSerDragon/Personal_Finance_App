package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.WalletService;
import com.example.finance.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class WalletServiceTest {

    @Test
    public void testAddIncomeAndExpenseBalance() {
        UserService us = new UserService();
        us.register("testuser", "pass1234");
        User u = us.login("testuser", "pass1234");
        WalletService ws = new WalletService();

        // Добавляем доход
        ws.addIncome(u, "Зарплата:1000:ok");

        // Симулируем ввод "y" для подтверждения создания категории
        String simulatedInput = "y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Добавляем расход
        ws.addExpense(u, "Еда:200:lunch", scanner);

        assertEquals(800.0, u.getWallet().getBalance(), 0.0001);
    }
}