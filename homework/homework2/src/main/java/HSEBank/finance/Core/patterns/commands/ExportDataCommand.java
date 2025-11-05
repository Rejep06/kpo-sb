package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.interfaces.ICommand;
import HSEBank.finance.Core.services.ExportService;
import java.util.UUID;

public class ExportDataCommand implements ICommand {
    private final ExportService exportService;
    private final String format;
    private final String filePath;
    private final UUID accountId;

    public ExportDataCommand(ExportService exportService, String format, String filePath, UUID accountId) {
        this.exportService = exportService;
        this.format = format;
        this.filePath = filePath;
        this.accountId = accountId;
    }

    @Override
    public void execute() {
        exportService.exportData(format, filePath, accountId);
    }

    @Override
    public void undo() {

    }

    @Override
    public String getDescription() {
        return "Export data to " + format + " format: " + filePath;
    }
}