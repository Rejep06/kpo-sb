package hsebank.finance.core.services;

import hsebank.finance.core.domain.interfaces.DataProvider;
import hsebank.finance.core.domain.interfaces.StructuredFinancialVisitor;
import hsebank.finance.core.patterns.visitors.CsvExportVisitor;
import hsebank.finance.core.patterns.visitors.JsonExportVisitor;
import hsebank.finance.core.patterns.visitors.YamlExportVisitor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;


@Service
public class ExportService {
    private final DataProvider dataProvider;
    private final Map<String, Supplier<StructuredFinancialVisitor>> visitorStrategies;

    public ExportService(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.visitorStrategies = new HashMap<>();
        initializeStrategies();
    }

    private void initializeStrategies() {
        visitorStrategies.put("json", JsonExportVisitor::new);
        visitorStrategies.put("csv", CsvExportVisitor::new);
        visitorStrategies.put("yaml", YamlExportVisitor::new);
    }

    public void registerFormat(String format, Supplier<StructuredFinancialVisitor> visitorSupplier) {
        visitorStrategies.put(format.toLowerCase(), visitorSupplier);
    }

    public void exportData(String format, String filePath, UUID accountId) {
        try {
            Supplier<StructuredFinancialVisitor> strategy = visitorStrategies.get(format.toLowerCase());
            if (strategy == null) {
                throw new IllegalArgumentException("Unsupported format: " + format);
            }

            StructuredFinancialVisitor visitor = strategy.get();
            String content = exportData(visitor, accountId);

            Path outputPath = Path.of(filePath);
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, content);

            System.out.println("Data exported successfully to: " + filePath);

        } catch (IOException e) {
            throw new RuntimeException("Export failed: " + e.getMessage(), e);
        }
    }

    private String exportData(StructuredFinancialVisitor visitor, UUID accountId) {
        // Экспорт счетов
        visitor.startArray("accounts");
        dataProvider.getAccounts(accountId).forEach(account -> account.accept(visitor));
        visitor.endArray();

        // Экспорт категорий
        visitor.startArray("categories");
        dataProvider.getAllCategories().forEach(category -> category.accept(visitor));
        visitor.endArray();

        // Экспорт операций
        visitor.startArray("operations");
        dataProvider.getOperations(accountId).forEach(operation -> operation.accept(visitor));
        visitor.endArray();

        return visitor.getResult();
    }
}