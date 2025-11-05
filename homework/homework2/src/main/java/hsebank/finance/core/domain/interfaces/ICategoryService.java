package hsebank.finance.core.domain.interfaces;

import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.enums.OperationType;
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