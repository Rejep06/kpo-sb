package HSEBank.finance.Core.patterns.template;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import org.springframework.stereotype.Component;

@Component
public class CsvDataImporter extends DataImporter {

    public CsvDataImporter(IRepository<BankAccount> accountRepository,
                           IRepository<Category> categoryRepository,
                           IRepository<Operation> operationRepository) {
        super(accountRepository, categoryRepository, operationRepository);
    }

    @Override
    protected ParsedData parseData(String content) {
        ParsedData result = new ParsedData();
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) { // Skip header
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] fields = line.split(",");
            if (fields.length < 3) continue;

            String type = fields[0].trim();

            try {
                switch (type.toLowerCase()) {
                    case "account":
                        if (fields.length >= 4) {
                            BankAccount account = new BankAccount(
                                parseUUID(fields[1].trim()),
                                fields[2].trim(),
                                Double.parseDouble(fields[3].trim())
                            );
                            result.addAccount(account);
                        }
                        break;

                    case "category":
                        if (fields.length >= 4) {
                            Category category = new Category(
                                parseUUID(fields[1].trim()),
                                parseOperationType(fields[2].trim()),
                                fields[3].trim()
                            );
                            result.addCategory(category);
                        }
                        break;

                    case "operation":
                        if (fields.length >= 8) {
                            Operation operation = new Operation(
                                parseUUID(fields[1].trim()),
                                parseOperationType(fields[2].trim()),
                                parseUUID(fields[3].trim()),
                                Double.parseDouble(fields[4].trim()),
                                parseDateTime(fields[5].trim()),
                                parseUUID(fields[6].trim()),
                                fields[7].trim()
                            );
                            result.addOperation(operation);
                        }
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error parsing CSV line: " + line + " - " + e.getMessage());
            }
        }

        return result;
    }

    @Override
    public boolean supportsFormat(String format) {
        return "csv".equalsIgnoreCase(format);
    }
}