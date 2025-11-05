package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.interfaces.IBankAccountService;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAccountCommand extends BaseCommand {
    private final IBankAccountService accountService;
    private final UUID accountId;
    private final String newName;
    private final double newBalance;
    private final String oldName;
    private final double oldBalance;
    private BankAccount updatedAccount;

    public UpdateAccountCommand(IBankAccountService accountService, UUID accountId,
                                String newName, double newBalance) {
        super("Update bank account: " + accountId);
        this.accountService = accountService;
        this.accountId = accountId;
        this.newName = newName;
        this.newBalance = newBalance;

        // Сохраняем старое состояние для undo
        BankAccount oldAccount = accountService.getAccount(accountId);
        this.oldName = oldAccount.getName();
        this.oldBalance = oldAccount.getBalance();
    }

    @Override
    public void execute() {
        updatedAccount = accountService.updateAccount(accountId, newName, newBalance);
    }

    @Override
    public void undo() {
        accountService.updateAccount(accountId, oldName, oldBalance);
    }
}