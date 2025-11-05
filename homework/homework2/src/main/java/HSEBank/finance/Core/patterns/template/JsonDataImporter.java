package HSEBank.finance.Core.patterns.template;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class JsonDataImporter extends DataImporter {

    public JsonDataImporter(IRepository<BankAccount> accountRepository,
                            IRepository<Category> categoryRepository,
                            IRepository<Operation> operationRepository) {
        super(accountRepository, categoryRepository, operationRepository);
    }

    @Override
    protected ParsedData parseData(String content) {
        try {
            ParsedData result = new ParsedData();

            // Упрощенный парсинг JSON с помощью регулярных выражений
            parseAccounts(content, result);
            parseCategories(content, result);
            parseOperations(content, result);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON: " + e.getMessage(), e);
        }
    }

    private void parseAccounts(String content, ParsedData result) {
        // Ищем блок accounts
        Pattern accountsPattern = Pattern.compile("\"accounts\"\\s*:\\s*\\[([^\\]]+)\\]");
        Matcher accountsMatcher = accountsPattern.matcher(content);

        if (accountsMatcher.find()) {
            String accountsContent = accountsMatcher.group(1);
            // Ищем отдельные объекты счетов
            Pattern accountPattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher accountMatcher = accountPattern.matcher(accountsContent);

            while (accountMatcher.find()) {
                String accountData = accountMatcher.group(1);
                try {
                    BankAccount account = parseAccount(accountData);
                    result.addAccount(account);
                } catch (Exception e) {
                    System.err.println("Error parsing account: " + e.getMessage());
                }
            }
        }
    }

    private BankAccount parseAccount(String accountData) {
        String id = extractField(accountData, "id");
        String name = extractField(accountData, "name");
        String balance = extractField(accountData, "balance");

        return new BankAccount(
            parseUUID(id),
            name.replace("\"", "").trim(),
            Double.parseDouble(balance)
        );
    }

    private void parseCategories(String content, ParsedData result) {
        Pattern categoriesPattern = Pattern.compile("\"categories\"\\s*:\\s*\\[([^\\]]+)\\]");
        Matcher categoriesMatcher = categoriesPattern.matcher(content);

        if (categoriesMatcher.find()) {
            String categoriesContent = categoriesMatcher.group(1);
            Pattern categoryPattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher categoryMatcher = categoryPattern.matcher(categoriesContent);

            while (categoryMatcher.find()) {
                String categoryData = categoryMatcher.group(1);
                try {
                    Category category = parseCategory(categoryData);
                    result.addCategory(category);
                } catch (Exception e) {
                    System.err.println("Error parsing category: " + e.getMessage());
                }
            }
        }
    }

    private Category parseCategory(String categoryData) {
        String id = extractField(categoryData, "id");
        String type = extractField(categoryData, "type");
        String name = extractField(categoryData, "name");

        return new Category(
            parseUUID(id),
            parseOperationType(type.replace("\"", "").trim()),
            name.replace("\"", "").trim()
        );
    }

    private void parseOperations(String content, ParsedData result) {
        Pattern operationsPattern = Pattern.compile("\"operations\"\\s*:\\s*\\[([^\\]]+)\\]");
        Matcher operationsMatcher = operationsPattern.matcher(content);

        if (operationsMatcher.find()) {
            String operationsContent = operationsMatcher.group(1);
            Pattern operationPattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher operationMatcher = operationPattern.matcher(operationsContent);

            while (operationMatcher.find()) {
                String operationData = operationMatcher.group(1);
                try {
                    Operation operation = parseOperation(operationData);
                    result.addOperation(operation);
                } catch (Exception e) {
                    System.err.println("Error parsing operation: " + e.getMessage());
                }
            }
        }
    }

    private Operation parseOperation(String operationData) {
        String id = extractField(operationData, "id");
        String type = extractField(operationData, "type");
        String bankAccountId = extractField(operationData, "bankAccountId");
        String amount = extractField(operationData, "amount");
        String date = extractField(operationData, "date");
        String categoryId = extractField(operationData, "categoryId");
        String description = extractOptionalField(operationData, "description");

        return new Operation(
            parseUUID(id),
            parseOperationType(type.replace("\"", "").trim()),
            parseUUID(bankAccountId),
            Double.parseDouble(amount),
            parseDateTime(date.replace("\"", "").trim()),
            parseUUID(categoryId),
            description != null ? description.replace("\"", "").trim() : null
        );
    }

    private String extractField(String data, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"?([^\",}]+)\"?");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Field '" + fieldName + "' not found in: " + data);
    }

    private String extractOptionalField(String data, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"?([^\",}]+)\"?");
        Matcher matcher = pattern.matcher(data);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Override
    public boolean supportsFormat(String format) {
        return "json".equalsIgnoreCase(format);
    }
}