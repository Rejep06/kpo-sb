package HSEBank.finance.Core.domain.interfaces;

public interface IFinancialCommand extends ICommand {
    boolean canExecute();
    String validate();
}