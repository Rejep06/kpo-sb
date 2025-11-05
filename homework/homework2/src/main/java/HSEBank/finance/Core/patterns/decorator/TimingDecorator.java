package HSEBank.finance.Core.patterns.decorator;

import HSEBank.finance.Core.domain.interfaces.ICommand;

public class TimingDecorator implements ICommand {
    private final ICommand command;

    public TimingDecorator(ICommand command) {
        this.command = command;
    }

    @Override
    public void execute() {
        long startTime = System.currentTimeMillis();
        command.execute();
        long endTime = System.currentTimeMillis();
        System.out.println("Command '" + command.getDescription() +
                "' executed in " + (endTime - startTime) + "ms");
    }

    @Override
    public void undo() {
        command.undo();
    }

    @Override
    public String getDescription() {
        return command.getDescription();
    }
}