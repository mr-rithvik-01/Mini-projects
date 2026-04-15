import java.util.ArrayList;
import java.util.Scanner;

class InSufficientFundsException extends Exception {
    public InSufficientFundsException(String message) {
        super(message);
    }
}

class WalletAccount {
    private String accountHolder;
    private double balance;
    private ArrayList<Double> recentTransactions;
    public WalletAccount(String accountHolder, double openingBalance) {
        this.accountHolder = accountHolder;
        this.balance = openingBalance;
        this.recentTransactions = new ArrayList<>();
    }

    public void depositMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be positive."
            );
        }
        balance += amount;
        recordTransaction(amount);
        System.out.println(
                "Deposit successful. Current balance: " + balance
        );
    }

    public void processTransaction(double amount)
            throws InSufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be positive."
            );
        }
        if (amount > balance) {
            throw new InSufficientFundsException(
                    "Insufficient balance for this transaction."
            );
        }
        balance -= amount;
        recordTransaction(-amount);
        System.out.println(
                "Payment successful. Remaining balance: " + balance
        );
    }
    private void recordTransaction(double amount) {
        recentTransactions.add(amount);
        if (recentTransactions.size() > 5) {
            recentTransactions.remove(0);
        }
    }
    public void printMiniStatement() {
        System.out.println("\n====== Mini Statement ======");
        System.out.println("Account Holder: " + accountHolder);
        if (recentTransactions.isEmpty()) {
            System.out.println("No recent transactions.");
            return;
        }
        for (double amount : recentTransactions) {
            if (amount > 0) {
                System.out.println("Deposit: " + amount);
            } else {
                System.out.println("Spend: " + Math.abs(amount));
            }
        }
        System.out.println("Current Balance: " + balance);
        System.out.println("============================");
    }
}

public class FinSafeApp {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter account holder name: ");
        String holderName = userInput.nextLine();
        System.out.print("Enter opening balance: ₹");
        double openingBalance = userInput.nextDouble();
        WalletAccount userWallet =
                new WalletAccount(holderName, openingBalance);
        boolean continueApp = true;
        while (continueApp) {
            System.out.println("\n====== FinSafe Menu ======");
            System.out.println("1. Deposit Money");
            System.out.println("2. Spend Money");
            System.out.println("3. View Mini Statement");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            int userChoice = userInput.nextInt();
            try {
                switch (userChoice) {
                  case 1:
                        System.out.print("Enter deposit amount: ");
                        double depositAmount = userInput.nextDouble();
                        userWallet.depositMoney(depositAmount);
                        break;
                    case 2:
                        System.out.print("Enter spend amount: ");
                        double spendAmount = userInput.nextDouble();
                        userWallet.processTransaction(spendAmount);
                        break;
                    case 3:
                        userWallet.printMiniStatement();
                        break;
                    case 4:
                        continueApp = false;
                        System.out.println(
                                "Thank you for using FinSafe."
                        );
                        break;
                    default:
                        System.out.println(
                                "Invalid menu choice."
                        );
                }
            } catch (InSufficientFundsException error) {
                System.out.println(
                        "Transaction Failed: " + error.getMessage()
                );
            } catch (IllegalArgumentException error) {
                System.out.println(
                        "Input Error: " + error.getMessage()
                );
            } catch (Exception error) {
                System.out.println(
                        "Unexpected error occurred."
                );
            }
        }
        userInput.close();
    }
}