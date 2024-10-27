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

import model.BankAccount;
import service.BankService;

public class BankingUI {
    private JFrame frame;                // Main application frame
    private BankService bankService;     // Service to manage bank operations
    private String currentUser;          // Stores the username of the logged-in user

    public BankingUI() {
        bankService = new BankService(); // Initialize the BankService
        createLoginScreen();             // Display the login screen
    }

    // Method to create the login screen
    private void createLoginScreen() {
        frame = new JFrame("Banking Application");  // Initialize frame with a title

        // Set up the panel with GridBagLayout for flexible component arrangement
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);        // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;   // Set fill to horizontal

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);                  // Add username label to the panel

        // Username Field
        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);              // Add username input field to the panel

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);                  // Add password label to the panel

        // Password Field
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);              // Add password input field to the panel

        // Login Button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(loginButton, gbc);                // Add login button to the panel

        // Signup Button
        JButton signupButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(signupButton, gbc);               // Add signup button to the panel

        frame.add(panel);                           // Add the panel to the frame
        frame.setSize(400, 200);                    // Set frame size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit app on close
        frame.setVisible(true);                     // Make frame visible

        // Action listener for login button
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Check if username or password is empty
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty!");
                return;
            }

            // Attempt to log in through bankService
            BankAccount account = bankService.login(username, password);
            if (account != null) {                  // If login is successful
                currentUser = username;             // Set the current user
                createBankingScreen();              // Show banking screen
            } else {                                // If login fails
                JOptionPane.showMessageDialog(frame, "Invalid login! Please check your credentials.");
            }
        });

        // Action listener for signup button
        signupButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Check if username or password is empty
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty!");
                return;
            }

            // Check if password meets length requirement
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters long!");
                return;
            }

            // Attempt to create account through bankService
            if (bankService.createAccount(username, password)) {
                JOptionPane.showMessageDialog(frame, "Account created successfully! Please log in.");
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists or invalid input! Please choose a different username.");
            }
        });
    }

    // Method to create the main banking screen after login
    private void createBankingScreen() {
        frame.getContentPane().removeAll();     // Clear the frame's content
        frame.repaint();                        // Refresh the frame

        // Display current balance for the logged-in user
        JLabel balanceLabel = new JLabel("Balance: " + bankService.checkBalance(currentUser));
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton logoutButton = new JButton("Logout");

        // Set up panel to hold buttons and balance label
        JPanel panel = new JPanel();
        panel.add(balanceLabel);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(logoutButton);

        frame.add(panel);                       // Add panel to the frame
        frame.revalidate();                     // Refresh the frame to display new components

        // Action listener for deposit button
        depositButton.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog("Enter amount to deposit:");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Amount cannot be empty!");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {              // Check if deposit amount is positive
                    JOptionPane.showMessageDialog(frame, "Amount must be greater than zero!");
                    return;
                }

                bankService.deposit(currentUser, amount);  // Perform deposit
                balanceLabel.setText("Balance: " + bankService.checkBalance(currentUser));  // Update balance display
            } catch (NumberFormatException ex) { // Catch invalid input
                JOptionPane.showMessageDialog(frame, "Invalid amount! Please enter a valid number.");
            }
        });

        // Action listener for withdraw button
        withdrawButton.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog("Enter amount to withdraw:");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Amount cannot be empty!");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {              // Check if withdraw amount is positive
                    JOptionPane.showMessageDialog(frame, "Amount must be greater than zero!");
                    return;
                }

                // Attempt to withdraw and check if successful
                if (bankService.withdraw(currentUser, amount)) {
                    balanceLabel.setText("Balance: " + bankService.checkBalance(currentUser));  // Update balance display
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient funds or invalid input!");
                }
            } catch (NumberFormatException ex) { // Catch invalid input
                JOptionPane.showMessageDialog(frame, "Invalid amount! Please enter a valid number.");
            }
        });

        // Action listener for logout button
        logoutButton.addActionListener(e -> {
            currentUser = null;                 // Clear current user
            createLoginScreen();                // Show login screen
        });
    }

    // Main method to start the application
    public static void main(String[] args) {
        new BankingUI();                        // Initialize the banking UI
    }
}
