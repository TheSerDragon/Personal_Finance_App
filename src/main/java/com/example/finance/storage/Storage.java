package com.example.finance.storage;

import com.example.finance.model.Wallet;
import java.util.Map;

public interface Storage {

    /** Сохраняет всех пользователей (логин/пароль) */
    void persistUsers(Map<String, String> users);

    /** Загружает всех пользователей */
    Map<String, String> loadUsers();

    /** Сохраняет кошелёк конкретного пользователя */
    void saveWallet(String username, Wallet wallet);

    /** Загружает кошелёк конкретного пользователя */
    Wallet loadWallet(String username);
}