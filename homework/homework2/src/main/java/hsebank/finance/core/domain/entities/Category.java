package hsebank.finance.core.domain.entities;

import hsebank.finance.core.domain.enums.OperationType;
import hsebank.finance.core.domain.interfaces.FinancialVisitor;
import hsebank.finance.core.domain.interfaces.IEntity;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Category implements IEntity {
    private final UUID id;
    private OperationType operationType;
    private String name;

    public Category(UUID id, OperationType operationType, String name) {
        this.id = id;
        this.operationType = operationType;
        this.name = name;
    }

    public void update(OperationType operationType, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        this.operationType = operationType;
        this.name = name;
    }

    public void accept(FinancialVisitor visitor) {
        visitor.visit(this);
    }
}