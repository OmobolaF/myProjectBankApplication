package service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import model.BankAccount;

public class BankService {
    private Map<String, BankAccount> accounts;       // Stores user accounts with usernames as keys
    private static final String FILE_PATH = "src/main/resources/accounts.txt";  // File path for account data

    public BankService() {
        accounts = loadAccounts();                   // Load accounts from file on initialization
    }

    // Method to create a new account
    public boolean createAccount(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;                            // Return false if username or password is empty
        }
        if (!accounts.containsKey(username)) {       // Check if username is unique
            BankAccount newAccount = new BankAccount(username, password);
            accounts.put(username, newAccount);      // Add new account to accounts map
            saveAccounts();                          // Save accounts to file
            return true;
        }
        return false;                                // Return false if username already exists
    }

    // Method to log in to an account
    public BankAccount login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;                             // Return null if username or password is empty
        }
        BankAccount account = accounts.get(username);        // Retrieve account by username
        if (account != null && account.password().equals(password)) {
            return account;                          // Return account if password matches
        }
        return null;                                 // Return null if login fails
    }

    // Method to load accounts from file
    private Map<String, BankAccount> loadAccounts() {
        if (!Files.exists(Path.of(FILE_PATH))) {     // Check if file exists
            return new HashMap<>();                  // Return empty map if file does not exist
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<String, BankAccount>) in.readObject();  // Read accounts map from file
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();                  // Return empty map if an error occurs
        }
    }

    // Method to save accounts to file
    private void saveAccounts() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(accounts);               // Write accounts map to file
        } catch (IOException e) {
            e.printStackTrace();                     // Print stack trace if an error occurs
        }
    }

    // Method to deposit an amount into a user's account
    public void deposit(String username, double amount) {
        if (username == null || username.isEmpty() || amount <= 0) {
            return;                                  // Return if username is empty or amount is invalid
        }
        BankAccount account = accounts.get(username);        // Retrieve account by username
        if (account != null) {
            accounts.put(username, account.deposit(amount));  // Update account with new balance
            saveAccounts();                          // Save updated accounts to file
        }
    }

    // Method to withdraw an amount from a user's account
    public boolean withdraw(String username, double amount) {
        if (username == null || username.isEmpty() || amount <= 0) {
            return false;                            // Return false if username is empty or amount is invalid
        }
        BankAccount account = accounts.get(username);        // Retrieve account by username
        if (account == null) {
            return false;                            // Return false if account does not exist
        }
        BankAccount updatedAccount = account.withdraw(amount); // Attempt to withdraw amount
        if (updatedAccount.balance() != account.balance()) { // Check if balance was updated
            accounts.put(username, updatedAccount);  // Update account with new balance
            saveAccounts();                          // Save updated accounts to file
            return true;
        }
        return false;                                // Return false if withdrawal was unsuccessful
    }

    // Method to check the balance of a user's account
    public double checkBalance(String username) {
        if (username == null || username.isEmpty()) {
            return 0.0;                              // Return 0.0 if username is empty
        }
        BankAccount account = accounts.get(username);        // Retrieve account by username
        return account != null ? account.balance() : 0.0;  // Return balance if account exists, else 0.0
    }
}
