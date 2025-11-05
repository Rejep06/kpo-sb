package hsebank.finance.core.domain.interfaces;

public interface ICommand {
    void execute();

    void undo();

    String getDescription();
}