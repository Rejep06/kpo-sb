package hsebank.finance.core.patterns.template;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class YamlDataImporter extends DataImporter {

    public YamlDataImporter(IRepository<BankAccount> accountRepository,
                            IRepository<Category> categoryRepository,
                            IRepository<Operation> operationRepository) {
        super(accountRepository, categoryRepository, operationRepository);
    }

    @Override
    protected ParsedData parseData(String content) {
        try {
            ParsedData result = new ParsedData();

            // Упрощенный парсинг YAML
            parseYamlAccounts(content, result);
            parseYamlCategories(content, result);
            parseYamlOperations(content, result);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing YAML: " + e.getMessage(), e);
        }
    }

    private void parseYamlAccounts(String content, ParsedData result) {
        Pattern accountsSection = Pattern.compile("accounts:\\s*\\n(\\s+-.*?\\n)+");
        Matcher matcher = accountsSection.matcher(content);

        if (matcher.find()) {
            String accountsContent = matcher.group(0);
            Pattern accountPattern =
                    Pattern.compile("-\\s*\\n\\s+id:\\s*(.+)\\n\\s+name:\\s*(.+)\\n\\s+balance:\\s*(.+)");
            Matcher accountMatcher = accountPattern.matcher(accountsContent);

            while (accountMatcher.find()) {
                try {
                    BankAccount account = new BankAccount(
                            parseUUID(accountMatcher.group(1).trim()),
                            accountMatcher.group(2).trim(),
                            Double.parseDouble(accountMatcher.group(3).trim())
                    );
                    result.addAccount(account);
                } catch (Exception e) {
                    System.err.println("Error parsing YAML account: " + e.getMessage());
                }
            }
        }
    }

    private void parseYamlCategories(String content, ParsedData result) {
        Pattern categoriesSection = Pattern.compile("categories:\\s*\\n(\\s+-.*?\\n)+");
        Matcher matcher = categoriesSection.matcher(content);

        if (matcher.find()) {
            String categoriesContent = matcher.group(0);
            Pattern categoryPattern =
                    Pattern.compile("-\\s*\\n\\s+id:\\s*(.+)\\n\\s+type:\\s*(.+)\\n\\s+name:\\s*(.+)");
            Matcher categoryMatcher = categoryPattern.matcher(categoriesContent);

            while (categoryMatcher.find()) {
                try {
                    Category category = new Category(
                            parseUUID(categoryMatcher.group(1).trim()),
                            parseOperationType(categoryMatcher.group(2).trim()),
                            categoryMatcher.group(3).trim()
                    );
                    result.addCategory(category);
                } catch (Exception e) {
                    System.err.println("Error parsing YAML category: " + e.getMessage());
                }
            }
        }
    }

    private void parseYamlOperations(String content, ParsedData result) {
        Pattern operationsSection = Pattern.compile("operations:\\s*\\n(\\s+-.*?\\n)+");
        Matcher matcher = operationsSection.matcher(content);

        if (matcher.find()) {
            String operationsContent = matcher.group(0);
            // Более гибкий паттерн для операций (description опционально)
            Pattern operationPattern = Pattern.compile(
                    "-\\s*\\n\\s+id:\\s*(.+)\\n\\s+type:\\s*(.+)\\n\\s+bankAccountId:\\s*(.+)\\n\\s+amount:"
                            + "\\s*(.+)\\n\\s+date:\\s*(.+)\\n\\s+categoryId:\\s*(.+)(\\n\\s+description:\\s*(.+))?"
            );
            Matcher operationMatcher = operationPattern.matcher(operationsContent);

            while (operationMatcher.find()) {
                try {
                    String description = operationMatcher.group(8); // description может быть null
                    Operation operation = new Operation(
                            parseUUID(operationMatcher.group(1).trim()),
                            parseOperationType(operationMatcher.group(2).trim()),
                            parseUUID(operationMatcher.group(3).trim()),
                            Double.parseDouble(operationMatcher.group(4).trim()),
                            parseDateTime(operationMatcher.group(5).trim()),
                            parseUUID(operationMatcher.group(6).trim()),
                            description != null ? description.trim() : null
                    );
                    result.addOperation(operation);
                } catch (Exception e) {
                    System.err.println("Error parsing YAML operation: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean supportsFormat(String format) {
        return "yaml".equalsIgnoreCase(format) || "yml".equalsIgnoreCase(format);
    }
}