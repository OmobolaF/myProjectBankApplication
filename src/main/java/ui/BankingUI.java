package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import service.BankService;

public class BankingUI {
    private JFrame frame;
    private BankService bankService;
    private String currentUser;

    public BankingUI() {
        bankService = new BankService();
        createLoginScreen();
    }

    private void createLoginScreen() {
        frame = new JFrame("Banking Application");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        // Username Field
        var usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        // Password Label
        var passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        // Password Field
        var passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        // Login Button
        var loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(loginButton, gbc);

        // Signup Button
        var signupButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(signupButton, gbc);

        frame.add(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        loginButton.addActionListener(e -> {
            var username = usernameField.getText().trim();
            var password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty!");
                return;
            }

            var account = bankService.login(username, password);
            if (account != null) {
                currentUser = username;
                createBankingScreen();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login! Please check your credentials.");
            }
        });

        signupButton.addActionListener(e -> {
            var username = usernameField.getText().trim();
            var password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty!");
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters long!");
                return;
            }

            if (bankService.createAccount(username, password)) {
                JOptionPane.showMessageDialog(frame, "Account created successfully! Please log in.");
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists or invalid input! Please choose a different username.");
            }
        });
    }

    private void createBankingScreen() {
        frame.getContentPane().removeAll();
        frame.repaint();

        var balanceLabel = new JLabel("Balance: " + bankService.checkBalance(currentUser));
        var depositButton = new JButton("Deposit");
        var withdrawButton = new JButton("Withdraw");
        var logoutButton = new JButton("Logout");

        var panel = new JPanel();
        panel.add(balanceLabel);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(logoutButton);

        frame.add(panel);
        frame.revalidate();

        depositButton.addActionListener(e -> {
            var amountStr = JOptionPane.showInputDialog("Enter amount to deposit:");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Amount cannot be empty!");
                return;
            }

            try {
                var amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(frame, "Amount must be greater than zero!");
                    return;
                }

                bankService.deposit(currentUser, amount);
                balanceLabel.setText("Balance: " + bankService.checkBalance(currentUser));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount! Please enter a valid number.");
            }
        });

        withdrawButton.addActionListener(e -> {
            var amountStr = JOptionPane.showInputDialog("Enter amount to withdraw:");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Amount cannot be empty!");
                return;
            }

            try {
                var amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(frame, "Amount must be greater than zero!");
                    return;
                }

                if (bankService.withdraw(currentUser, amount)) {
                    balanceLabel.setText("Balance: " + bankService.checkBalance(currentUser));
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient funds or invalid input!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount! Please enter a valid number.");
            }
        });

        logoutButton.addActionListener(e -> {
            currentUser = null;
            createLoginScreen();
        });
    }

    public static void main(String[] args) {
        new BankingUI();
    }
}
