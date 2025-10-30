package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

public class BudgetLimitAlertTest {

    @Test
    public void testBudgetLimitExceeded() {
        UserService us = new UserService();
        us.register("testBudget", "1234");
        User user = us.login("testBudget", "1234");

        BudgetService bs = new BudgetService();
        WalletService ws = new WalletService();

        bs.addCategory(user, "Еда");
        bs.setBudget(user, "Еда:500");

        // Добавляем расход больше бюджета
        String simulatedInput = "y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        ws.addExpense(user, "Еда:700:ужин", scanner);

        double remaining = user.getWallet().getRemainingBudget("Еда");
        assertTrue(remaining < 0, "Бюджет должен быть превышен");
    }
}