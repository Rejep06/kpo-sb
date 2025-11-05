package HSEBank.finance.Core.domain.interfaces;

import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.enums.OperationType;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {
    Category createCategory(OperationType type, String name);
    Category getCategory(UUID id);
    Category updateCategory(UUID id, OperationType type, String name);
    void deleteCategory(UUID id);
    List<Category> getAllCategories();
    List<Category> getCategoriesByType(OperationType type);
    boolean categoryExists(UUID id);
}