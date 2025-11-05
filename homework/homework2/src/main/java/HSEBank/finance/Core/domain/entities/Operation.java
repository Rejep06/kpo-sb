package HSEBank.finance.Core.domain.entities;

import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.FinancialVisitor;
import HSEBank.finance.Core.domain.interfaces.IEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Operation implements IEntity {
    private final UUID id;
    private OperationType type;
    private UUID bankAccountId;
    private double amount;
    private LocalDateTime date;
    private UUID categoryId;
    private String description;

    public Operation(UUID id, OperationType type, UUID bankAccountId,
                     double amount, LocalDateTime date, UUID categoryId, String description) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.id = id;
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
        this.description = description;
    }

    public void update(OperationType type, UUID bankAccountId, double amount,
                       LocalDateTime date, UUID categoryId, String description) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
        this.description = description;
    }

    public void accept(FinancialVisitor visitor) {
        visitor.visit(this);
    }
}