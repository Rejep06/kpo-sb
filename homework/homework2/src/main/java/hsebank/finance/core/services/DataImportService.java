package hsebank.finance.core.services;

import hsebank.finance.core.patterns.template.DataImporter;
import hsebank.finance.core.patterns.template.CsvDataImporter;
import hsebank.finance.core.patterns.template.JsonDataImporter;
import hsebank.finance.core.patterns.template.YamlDataImporter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class DataImportService {
    private final List<DataImporter> importers;

    // Spring автоматически внедрит все бины, реализующие DataImporter
    public DataImportService(List<DataImporter> importers) {
        this.importers = importers;
    }

    public String importData(String format, String filePath) {
        DataImporter importer = findImporter(format);
        if (importer == null) {
            throw new IllegalArgumentException("Unsupported import format: " + format);
        }
        return importer.importData(filePath);
    }

    private DataImporter findImporter(String format) {
        return importers.stream()
                .filter(importer -> importer.supportsFormat(format))
                .findFirst()
                .orElse(null);
    }
}