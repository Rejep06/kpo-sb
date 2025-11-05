package HSEBank.finance.Core.domain.interfaces;

public interface StructuredFinancialVisitor extends FinancialVisitor {
    void startArray(String arrayName);
    void endArray();
    String getResult();
    void reset();
}