package hsebank.finance.core.patterns.visitors;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.interfaces.StructuredFinancialVisitor;

public class CsvExportVisitor implements StructuredFinancialVisitor {
    private final StringBuilder csvBuilder = new StringBuilder();
    private boolean headersWritten = false;

    @Override
    public void visit(BankAccount account) {
        if (!headersWritten) {
            csvBuilder.append("type,id,name,balance,operationType,bankAccountId,amount,date,categoryId,description\n");
            headersWritten = true;
        }

        csvBuilder.append("account,")
                .append(account.getId()).append(",")
                .append(escapeCsv(account.getName())).append(",")
                .append(account.getBalance()).append(",,,,\n");
    }

    @Override
    public void visit(Category category) {
        if (!headersWritten) {
            csvBuilder.append("type,id,name,balance,operationType,bankAccountId,amount,date,categoryId,description\n");
            headersWritten = true;
        }

        csvBuilder.append("category,")
                .append(category.getId()).append(",")
                .append(escapeCsv(category.getName())).append(",,,")
                .append(category.getOperationType()).append(",,,\n");
    }

    @Override
    public void visit(Operation operation) {
        if (!headersWritten) {
            csvBuilder.append("type,id,name,balance,operationType,bankAccountId,amount,date,categoryId,description\n");
            headersWritten = true;
        }

        csvBuilder.append("operation,")
                .append(operation.getId()).append(",,")
                .append(operation.getType()).append(",")
                .append(operation.getBankAccountId()).append(",")
                .append(operation.getAmount()).append(",")
                .append(operation.getDate()).append(",")
                .append(operation.getCategoryId()).append(",")
                .append(escapeCsv(operation.getDescription() != null ? operation.getDescription() : ""))
                .append("\n");
    }

    public String getResult() {
        return csvBuilder.toString();
    }

    @Override
    public void reset() {

    }

    public void startArray(String arrayName) {
    }

    public void endArray() {
    }

    private String escapeCsv(String text) {
        if (text == null) {
            return "";
        }
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }


}