package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.BudgetService;
import com.example.finance.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BudgetServiceTest {
    @Test
    public void testSetBudget() {
        UserService us = new UserService();
        us.register("user", "1234");
        User u = us.login("user", "1234");
        BudgetService bs = new BudgetService();
        bs.addCategory(u, "Еда");
        bs.setBudget(u, "Еда:4000");
        assertTrue(u.getWallet().getBudgets().containsKey("Еда"));
        assertEquals(4000.0, u.getWallet().getBudgets().get("Еда"));
    }
}