package hsebank.finance.core.domain.interfaces;

public interface IFinancialCommand extends ICommand {
    boolean canExecute();

    String validate();
}