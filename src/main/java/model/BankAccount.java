package model;

import java.io.Serializable;

public record BankAccount(String username, String password, double balance) implements Serializable {
    public BankAccount(String username, String password) {
        this(username, password, 0.0);
    }

    public BankAccount deposit(double amount) {
        if (amount > 0) {
            return new BankAccount(username, password, balance + amount);
        }
        return this;
    }

    public BankAccount withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            return new BankAccount(username, password, balance - amount);
        }
        return this;
    }
}
