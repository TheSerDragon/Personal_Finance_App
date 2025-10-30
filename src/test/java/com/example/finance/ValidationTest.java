package com.example.finance;

import com.example.finance.model.User;
import com.example.finance.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    @Test
    public void testInvalidBudgetInput() {
        UserService us = new UserService();
        us.register("ValUser1", "1111");
        User u = us.login("ValUser1", "1111");

        BudgetService bs = new BudgetService();
        bs.setBudget(u, "НеверныйФормат");

        // Проверяем, что бюджет не был добавлен
        assertTrue(u.getWallet().getBudgets().isEmpty(), "Бюджет не должен был добавиться при неверном формате");
    }
}
