package hsebank.finance.application;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.interfaces.IBankAccountService;
import hsebank.finance.core.patterns.commands.CreateAccountCommand;
import hsebank.finance.core.patterns.commands.UpdateAccountCommand;
import hsebank.finance.core.patterns.decorator.TimingDecorator;
import hsebank.finance.core.services.BalanceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountManagementService {
    private final IBankAccountService accountService;
    private final CLIMenuService menuService;
    private final InputValidator inputValidator;
    private final BalanceService balanceService;

    public void createAccount() {
        try {
            String name = menuService.getInput("Enter account name: ");
            if (!inputValidator.isValidName(name)) {
                menuService.showError("Account name cannot be empty");
                return;
            }

            double balance = inputValidator.getValidatedDouble("Enter initial balance: ", 0.0, 1_000_000.0);

            var createCmd = new CreateAccountCommand(accountService, name, balance);

            String validationError = createCmd.validate();
            if (validationError != null) {
                menuService.showError(validationError);
                return;
            }

            new TimingDecorator(createCmd).execute();
            BankAccount account = createCmd.getCreatedAccount();
            menuService.showSuccess("Account created: " + account.getName() + " (ID: " + account.getId() + ")");

        } catch (IllegalArgumentException e) {
            menuService.showError("Validation error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Account creation failed", e);
            menuService.showError("System error. Please try again.");
        }
    }

    public void showAllAccounts() {
        try {
            List<BankAccount> accounts = accountService.getAllAccounts();
            if (accounts.isEmpty()) {
                menuService.showMessage(" No accounts found");
                return;
            }

            menuService.showMessage("\n ACCOUNTS LIST:");
            menuService.showMessage("┌────────────────────────────────────┬────────────────────┬────────────┐");
            menuService.showMessage("│ ID                                 │ Name               │ Balance    │");
            menuService.showMessage("├────────────────────────────────────┼────────────────────┼────────────┤");

            for (BankAccount account : accounts) {
                String line = String.format("│ %-34s │ %-18s │ %10.2f │",
                        account.getId(),
                        account.getName(),
                        account.getBalance());
                menuService.showMessage(line);
            }
            menuService.showMessage("└────────────────────────────────────┴────────────────────┴────────────┘");

        } catch (Exception e) {
            log.error("Error loading accounts", e);
            menuService.showError("Failed to load accounts");
        }
    }

    public void findAccountById() {
        try {
            String id = inputValidator.getValidatedUUID("Enter account ID: ");
            BankAccount account = accountService.getAccount(UUID.fromString(id));

            menuService.showMessage("\n ACCOUNT DETAILS:");
            menuService.showMessage("ID: " + account.getId());
            menuService.showMessage("Name: " + account.getName());
            menuService.showMessage("Balance: " + account.getBalance());

        } catch (Exception e) {
            menuService.showError("Account not found: " + e.getMessage());
        }
    }

    public void updateAccount() {
        try {
            String id = inputValidator.getValidatedUUID("Enter account ID to update: ");
            String name = menuService.getInput("Enter new account name: ");
            double balance = inputValidator.getValidatedDouble("Enter new balance: ", 0.0, 1_000_000.0);

            var updateCmd = new UpdateAccountCommand(accountService, UUID.fromString(id), name, balance);
            new TimingDecorator(updateCmd).execute();

            menuService.showSuccess("Account updated successfully");

        } catch (IllegalArgumentException e) {
            menuService.showError("Validation error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Account update failed", e);
            menuService.showError("Failed to update account");
        }
    }

    public void deleteAccount() {
        try {
            String id = inputValidator.getValidatedUUID("Enter account ID to delete: ");
            accountService.deleteAccount(UUID.fromString(id));
            menuService.showSuccess("Account deleted successfully");

        } catch (IllegalArgumentException e) {
            menuService.showError("Account not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Account deletion failed", e);
            menuService.showError("Failed to delete account");
        }
    }

    public void recalculateAccountBalance() {
        try {
            String accountId = inputValidator.getValidatedUUID("Enter account ID to recalculate: ");
            double newBalance = balanceService.recalculateBalance(UUID.fromString(accountId));
            menuService.showSuccess("Balance recalculated: " + newBalance);
        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }
}