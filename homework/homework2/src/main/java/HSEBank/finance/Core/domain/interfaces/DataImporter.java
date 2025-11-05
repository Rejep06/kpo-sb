package HSEBank.finance.Core.domain.interfaces;

public interface DataImporter {
    String importData(String filePath);
    boolean supportsFormat(String format);
}