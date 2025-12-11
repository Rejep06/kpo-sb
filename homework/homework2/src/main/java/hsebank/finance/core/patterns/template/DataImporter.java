package hsebank.finance.core.patterns.template;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.enums.OperationType;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public abstract class DataImporter {
    protected final IRepository<BankAccount> accountRepository;
    protected final IRepository<Category> categoryRepository;
    protected final IRepository<Operation> operationRepository;

    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public DataImporter(IRepository<BankAccount> accountRepository,
                        IRepository<Category> categoryRepository,
                        IRepository<Operation> operationRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    public final String importData(String filePath) {
        try {
            String content = readFile(filePath);
            if (content == null || content.trim().isEmpty()) {
                return "File is empty or cannot be read: " + filePath;
            }

            ParsedData parsedData = parseData(content);
            return saveData(parsedData);
        } catch (Exception e) {
            return "Import failed for file " + filePath + ": " + e.getMessage();
        }
    }

    protected String readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.err.println("File not found: " + filePath);
                return null;
            }
            return Files.readString(path);
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    protected abstract ParsedData parseData(String content);

    protected String saveData(ParsedData data) {
        int accountsSaved = 0;
        int categoriesSaved = 0;
        int operationsSaved = 0;

        for (BankAccount account : data.getAccounts()) {
            if (!accountRepository.exists(account.getId())) {
                accountRepository.save(account);
                accountsSaved++;
            }
        }

        for (Category category : data.getCategories()) {
            if (!categoryRepository.exists(category.getId())) {
                categoryRepository.save(category);
                categoriesSaved++;
            }
        }

        for (Operation operation : data.getOperations()) {
            if (!operationRepository.exists(operation.getId())) {
                operationRepository.save(operation);
                operationsSaved++;
            }
        }

        return String.format("Imported: %d accounts, %d categories, %d operations",
                accountsSaved, categoriesSaved, operationsSaved);
    }

    protected UUID parseUUID(String uuidString) {
        return UUID.fromString(uuidString);
    }

    protected LocalDateTime parseDateTime(String dateString) {
        return LocalDateTime.parse(dateString, DATE_FORMATTER);
    }

    protected OperationType parseOperationType(String typeString) {
        return OperationType.valueOf(typeString.toUpperCase());
    }

    abstract public boolean supportsFormat(String format);
}