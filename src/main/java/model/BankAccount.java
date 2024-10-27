package model;

import java.io.Serializable;

// The BankAccount class represents a user’s bank account and implements Serializable for file storage
public record BankAccount(String username, String password, double balance) implements Serializable {

    // Constructor to create a new account with an initial balance of 0.0
    public BankAccount(String username, String password) {
        this(username, password, 0.0);
    }

    // Method to deposit an amount into the account
    // Returns a new BankAccount instance with the updated balance if the amount is valid
    public BankAccount deposit(double amount) {
        if (amount > 0) {  // Check if the deposit amount is positive
            return new BankAccount(username, password, balance + amount);  // Return a new account with updated balance
        }
        return this;       // Return the current account if the amount is invalid
    }

    // Method to withdraw an amount from the account
    // Returns a new BankAccount instance with the updated balance if withdrawal is possible
    public BankAccount withdraw(double amount) {
        if (amount > 0 && balance >= amount) {  // Check if amount is positive and sufficient balance exists
            return new BankAccount(username, password, balance - amount);  // Return a new account with updated balance
        }
        return this;       // Return the current account if the withdrawal is invalid
    }
}
