package HSEBank.finance.Core.domain.entities;

import HSEBank.finance.Core.domain.interfaces.FinancialVisitor;
import HSEBank.finance.Core.domain.interfaces.IEntity;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BankAccount implements IEntity {
    private final UUID id;
    private String name;
    private double balance;

    public BankAccount(UUID id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public void update(String name, double balance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.name = name;
        this.balance = balance;
    }

    public void accept(FinancialVisitor visitor) {
        visitor.visit(this);
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        this.balance -= amount;
    }
}