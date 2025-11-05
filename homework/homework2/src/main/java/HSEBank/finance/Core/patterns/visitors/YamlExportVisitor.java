package HSEBank.finance.Core.patterns.visitors;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.StructuredFinancialVisitor;

public class YamlExportVisitor implements StructuredFinancialVisitor {
    private final StringBuilder yamlBuilder = new StringBuilder();
    private int indentLevel = 0;

    public YamlExportVisitor() {
        yamlBuilder.append("---\n");
    }

    @Override
    public void visit(BankAccount account) {
        yamlBuilder.append(getIndent()).append("-\n");
        indentLevel++;
        yamlBuilder.append(getIndent()).append("id: ").append(account.getId()).append("\n");
        yamlBuilder.append(getIndent()).append("name: ").append(account.getName()).append("\n");
        yamlBuilder.append(getIndent()).append("balance: ").append(account.getBalance()).append("\n");
        indentLevel--;
    }

    @Override
    public void visit(Category category) {
        yamlBuilder.append(getIndent()).append("-\n");
        indentLevel++;
        yamlBuilder.append(getIndent()).append("id: ").append(category.getId()).append("\n");
        yamlBuilder.append(getIndent()).append("type: ").append(category.getOperationType()).append("\n");
        yamlBuilder.append(getIndent()).append("name: ").append(category.getName()).append("\n");
        indentLevel--;
    }

    @Override
    public void visit(Operation operation) {
        yamlBuilder.append(getIndent()).append("-\n");
        indentLevel++;
        yamlBuilder.append(getIndent()).append("id: ").append(operation.getId()).append("\n");
        yamlBuilder.append(getIndent()).append("type: ").append(operation.getType()).append("\n");
        yamlBuilder.append(getIndent()).append("bankAccountId: ").append(operation.getBankAccountId()).append("\n");
        yamlBuilder.append(getIndent()).append("amount: ").append(operation.getAmount()).append("\n");
        yamlBuilder.append(getIndent()).append("date: ").append(operation.getDate()).append("\n");
        yamlBuilder.append(getIndent()).append("categoryId: ").append(operation.getCategoryId()).append("\n");
        if (operation.getDescription() != null && !operation.getDescription().isEmpty()) {
            yamlBuilder.append(getIndent()).append("description: ").append(operation.getDescription()).append("\n");
        }
        indentLevel--;
    }

    public String getResult() {
        return yamlBuilder.toString();
    }

    @Override
    public void reset() {

    }

    public void startArray(String arrayName) {
        yamlBuilder.append(getIndent()).append(arrayName).append(":\n");
        indentLevel++;
    }

    public void endArray() {
        indentLevel--;
    }

    private String getIndent() {
        return "  ".repeat(Math.max(0, indentLevel));
    }
}