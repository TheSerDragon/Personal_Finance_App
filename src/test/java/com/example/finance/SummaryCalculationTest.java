package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SummaryCalculationTest {

    @Test
    public void testCategoryTotals() {
        UserService us = new UserService();
        us.register("summaryUser", "1234");
        User u = us.login("summaryUser", "1234");

        WalletService ws = new WalletService();
        ws.addIncome(u, "Зарплата:1000:test");
        ws.addExpense(u, "Еда:200:lunch", new java.util.Scanner(System.in));
        ws.addExpense(u, "Еда:100:coffee", new java.util.Scanner(System.in));

        double totalExp = u.getWallet().getTotalByTypeAndCategory("expense", "Еда");
        double totalInc = u.getWallet().getTotalByTypeAndCategory("income", "Зарплата");

        assertEquals(300.0, totalExp, 0.01);
        assertEquals(1000.0, totalInc, 0.01);
        assertEquals(700.0, u.getWallet().getBalance(), 0.01);
    }
}
