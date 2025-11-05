package HSEBank.finance.Core.domain.interfaces;

public interface ICommand {
    void execute();
    void undo();
    String getDescription();
}