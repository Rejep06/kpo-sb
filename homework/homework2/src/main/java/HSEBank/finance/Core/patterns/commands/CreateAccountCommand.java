package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.interfaces.IBankAccountService;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAccountCommand extends BaseCommand {
    private final IBankAccountService accountService;
    private final String accountName;
    private final double initialBalance;
    private BankAccount createdAccount;
    private UUID createdAccountId;

    public CreateAccountCommand(IBankAccountService accountService, String accountName, double initialBalance) {
        super("Create bank account: " + accountName);
        this.accountService = accountService;
        this.accountName = accountName;
        this.initialBalance = initialBalance;
    }

    @Override
    public String validate() {
        if (accountName == null || accountName.trim().isEmpty()) {
            return "Account name cannot be empty";
        }
        if (initialBalance < 0) {
            return "Initial balance cannot be negative";
        }
        return null;
    }

    @Override
    public void execute() {
        createdAccount = accountService.createAccount(accountName, initialBalance);
        createdAccountId = createdAccount.getId();
    }

    @Override
    public void undo() {
        if (createdAccountId != null) {
            accountService.deleteAccount(createdAccountId);
        }
    }
}