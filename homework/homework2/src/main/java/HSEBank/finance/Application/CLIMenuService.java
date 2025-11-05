package HSEBank.finance.Application;

import org.springframework.stereotype.Service;
import java.util.Scanner;

@Service
public class CLIMenuService {
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcomeMessage() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║           HSEBank Finance CLI        ║");
        System.out.println("║      Financial Management System     ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
    }

    public String showMainMenu() {
        System.out.println("\n MAIN MENU:");
        System.out.println("1. Account Management");
        System.out.println("2. Category Management");
        System.out.println("3. Operation Management");
        System.out.println("4. Analytics & Reports");
        System.out.println("5. Data Export");
        System.out.println("6. Data Import");
        System.out.println("7. System Tools");
        System.out.println("8. Pattern Demonstrations");
        System.out.println("0. Exit");
        System.out.print("\nSelect option: ");
        return scanner.nextLine();
    }

    public String showAccountManagementMenu() {
        System.out.println("\n ACCOUNT MANAGEMENT:");
        System.out.println("1. Create Account");
        System.out.println("2. List All Accounts");
        System.out.println("3. Find Account by ID");
        System.out.println("4. Update Account");
        System.out.println("5. Delete Account");
        System.out.println("6. Recalculate Balance");
        System.out.println("0. Back");
        System.out.print("\nSelect option: ");
        return scanner.nextLine();
    }

    public String showSystemToolsMenu() {
        System.out.println("\n SYSTEM TOOLS:");
        System.out.println("1. Recalculate All Balances");
        System.out.println("2. Cache Statistics");
        System.out.println("3. Clear Cache");
        System.out.println("0. Back");
        System.out.print("\nSelect option: ");
        return scanner.nextLine();
    }

    public String showImportMenu() {
        System.out.println("\n DATA IMPORT:");
        System.out.println("1. Import from JSON");
        System.out.println("2. Import from CSV");
        System.out.println("3. Import from YAML");
        System.out.println("0. Back");
        System.out.print("\nSelect option: ");
        return scanner.nextLine();
    }

    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String error) {
        System.out.println(" Error: " + error);
    }

    public void showSuccess(String message) {
        System.out.println("Success: " + message);
    }
}