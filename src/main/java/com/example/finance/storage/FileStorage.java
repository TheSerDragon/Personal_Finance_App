package com.example.finance.storage;

import com.example.finance.model.Wallet;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileStorage implements Storage {
    private static final String USERS_FILE = "users.dat";
    private static final String WALLETS_DIR = "wallets";

    public FileStorage() {
        try {
            Files.createDirectories(Path.of(WALLETS_DIR));
        } catch (IOException ignored) {}
    }

    @Override
    public void persistUsers(Map<String, String> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Не удалось сохранить файл пользователей: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) return (Map<String, String>) obj;
        } catch (Exception e) {
            System.out.println("Не удалось загрузить файл пользователей: " + e.getMessage());
        }
        return new HashMap<>();
    }

    @Override
    public void saveWallet(String username, Wallet wallet) {
        File out = new File(WALLETS_DIR, username + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out))) {
            oos.writeObject(wallet);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении кошелька: " + e.getMessage());
        }
    }

    @Override
    public Wallet loadWallet(String username) {
        File in = new File(WALLETS_DIR, username + ".ser");
        if (!in.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(in))) {
            Object obj = ois.readObject();
            if (obj instanceof Wallet) return (Wallet) obj;
        } catch (Exception e) {
            System.out.println("Не удалось загрузить кошелёк: " + e.getMessage());
        }
        return null;
    }
}