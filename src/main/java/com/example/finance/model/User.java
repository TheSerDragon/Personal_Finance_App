package com.example.finance.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String name;
    private final String password;
    private final Wallet wallet;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.wallet = new Wallet(name);
    }

    public String getName() { return name; }
    public String getPassword() { return password; }
    public Wallet getWallet() { return wallet; }
}