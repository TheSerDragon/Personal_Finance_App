package com.example.finance.service;

import com.example.finance.model.User;
import com.example.finance.storage.FileStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final FileStorage storage = new FileStorage();

    public UserService() {
        users.putAll(storage.loadUsers());
    }

    public void register(String username, String password) {
        if (username == null || username.isBlank()) { System.out.println("Логин не может быть пустым."); return; }
        if (password == null || password.length() < 4) { System.out.println("Пароль должен быть не менее 4 символов."); return; }
        if (users.containsKey(username)) { System.out.println("Пользователь с таким именем уже существует."); return; }
        users.put(username, password);
        storage.persistUsers(users);
        System.out.println("Пользователь зарегистрирован. Выполните 'login " + username + "' для входа.");
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank()) { System.out.println("Введите имя пользователя."); return null; }
        if (!users.containsKey(username)) { System.out.println("Пользователь не найден. Зарегистрируйтесь командой 'register'."); return null; }
        String pw = users.get(username);
        if (!pw.equals(password)) { System.out.println("Неверный пароль."); return null; }
        return new User(username, password);
    }

    public void persistUsers() {
        storage.persistUsers(users);
    }
}