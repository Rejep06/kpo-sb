package hsebank.finance.core.patterns.commands;

import hsebank.finance.core.domain.interfaces.IFinancialCommand;

public abstract class BaseCommand implements IFinancialCommand {
    protected String description;

    public BaseCommand(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean canExecute() {
        return validate() == null;
    }

    @Override
    public String validate() {
        return null; // По умолчанию валидация проходит
    }

    @Override
    public void undo() {
        // Базовая реализация - можно переопределить
    }
}