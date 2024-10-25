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
    private Map<String, BankAccount> accounts;
    private static final String FILE_PATH = "src/main/resources/accounts.txt";

    public BankService() {
        accounts = loadAccounts();
    }

    public boolean createAccount(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        if (!accounts.containsKey(username)) {
        	BankAccount newAccount = new BankAccount(username, password);
            accounts.put(username, newAccount);
            saveAccounts();
            return true;
        }
        return false;
    }

    public BankAccount login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        var account = accounts.get(username);
        if (account != null && account.password().equals(password)) {
            return account;
        }
        return null;
    }

    private Map<String, BankAccount> loadAccounts() {
        if (!Files.exists(Path.of(FILE_PATH))) {
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<String, BankAccount>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveAccounts() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deposit(String username, double amount) {
        if (username == null || username.isEmpty() || amount <= 0) {
            return;
        }
        var account = accounts.get(username);
        if (account != null) {
            accounts.put(username, account.deposit(amount));
            saveAccounts();
        }
    }

    public boolean withdraw(String username, double amount) {
        if (username == null || username.isEmpty() || amount <= 0) {
            return false;
        }
        var account = accounts.get(username);
        if (account == null) {
            return false;
        }
        var updatedAccount = account.withdraw(amount);
        if (updatedAccount.balance() != account.balance()) {
            accounts.put(username, updatedAccount);
            saveAccounts();
            return true;
        }
        return false;
    }

    public double checkBalance(String username) {
        if (username == null || username.isEmpty()) {
            return 0.0;
        }
        var account = accounts.get(username);
        return account != null ? account.balance() : 0.0;
    }
}