package HSEBank.finance.Core.services;

import HSEBank.finance.Core.patterns.template.DataImporter;
import HSEBank.finance.Core.patterns.template.CsvDataImporter;
import HSEBank.finance.Core.patterns.template.JsonDataImporter;
import HSEBank.finance.Core.patterns.template.YamlDataImporter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public boolean isFormatSupported(String format) {
        return findImporter(format) != null;
    }

    public List<String> getSupportedFormats() {
        return importers.stream()
                .map(importer -> {
                    if (importer instanceof JsonDataImporter) return "json";
                    if (importer instanceof CsvDataImporter) return "csv";
                    if (importer instanceof YamlDataImporter) return "yaml, yml";
                    return "unknown";
                })
                .collect(Collectors.toList());
    }
}