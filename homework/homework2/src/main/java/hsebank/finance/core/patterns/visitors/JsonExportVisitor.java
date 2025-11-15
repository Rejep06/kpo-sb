package hsebank.finance.core.patterns.visitors;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.interfaces.StructuredFinancialVisitor;

public class JsonExportVisitor implements StructuredFinancialVisitor {
    private final StringBuilder jsonBuilder = new StringBuilder();
    private boolean firstElementInArray = true;
    private boolean arrayStarted = false;

    public JsonExportVisitor() {
        jsonBuilder.append("{\n");
    }

    @Override
    public String getResult() {
        if (arrayStarted) {
            jsonBuilder.append("\n  ]");
        }
        jsonBuilder.append("\n}");
        return jsonBuilder.toString();
    }

    @Override
    public void reset() {
        jsonBuilder.setLength(0);
        jsonBuilder.append("{\n");
        firstElementInArray = true;
        arrayStarted = false;
    }

    @Override
    public void startArray(String arrayName) {
        if (arrayStarted) {
            jsonBuilder.append(",\n");
        }
        jsonBuilder.append("  \"").append(arrayName).append("\": [");
        firstElementInArray = true;
        arrayStarted = true;
    }

    @Override
    public void endArray() {
        jsonBuilder.append("\n  ]");
        firstElementInArray = true;
        arrayStarted = false;
    }

    @Override
    public void visit(BankAccount account) {
        if (!firstElementInArray) {
            jsonBuilder.append(",");
        }
        jsonBuilder.append("\n    {\n")
                .append("      \"id\": \"").append(account.getId()).append("\",\n")
                .append("      \"name\": \"").append(escapeJson(account.getName())).append("\",\n")
                .append("      \"balance\": ").append(account.getBalance()).append("\n")
                .append("    }");
        firstElementInArray = false;
    }

    @Override
    public void visit(Category category) {
        if (!firstElementInArray) {
            jsonBuilder.append(",");
        }
        jsonBuilder.append("\n    {\n")
                .append("      \"id\": \"").append(category.getId()).append("\",\n")
                .append("      \"type\": \"").append(category.getOperationType()).append("\",\n")
                .append("      \"name\": \"").append(escapeJson(category.getName())).append("\"\n")
                .append("    }");
        firstElementInArray = false;
    }

    @Override
    public void visit(Operation operation) {
        if (!firstElementInArray) {
            jsonBuilder.append(",");
        }
        jsonBuilder.append("\n    {\n")
                .append("      \"id\": \"").append(operation.getId()).append("\",\n")
                .append("      \"type\": \"").append(operation.getType()).append("\",\n")
                .append("      \"bankAccountId\": \"").append(operation.getBankAccountId()).append("\",\n")
                .append("      \"amount\": ").append(operation.getAmount()).append(",\n")
                .append("      \"date\": \"").append(operation.getDate()).append("\",\n")
                .append("      \"categoryId\": \"").append(operation.getCategoryId()).append("\",\n")
                .append("      \"description\": \"")
                .append(operation.getDescription() != null ? escapeJson(operation.getDescription()) : "")
                .append("\"\n")
                .append("    }");
        firstElementInArray = false;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}