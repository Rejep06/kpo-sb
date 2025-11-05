package HSEBank.finance.Core.services;

import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.ICategoryService;
import HSEBank.finance.Core.domain.interfaces.IFinancialFactory;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryFacade implements ICategoryService {
    private final IFinancialFactory factory;
    private final IRepository<Category> categoryRepository;

    public CategoryFacade(IFinancialFactory factory, IRepository<Category> categoryRepository){
        this.factory = factory;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(OperationType type, String name){
        Category category = factory.createCategory(type, name);
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
    }

    @Override
    public Category updateCategory(UUID id, OperationType type, String name) {
        Category category = getCategory(id);
        category.update(type, name);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        if (!categoryRepository.exists(id)) {
            throw new IllegalArgumentException("Category not found with ID: " + id);
        }
        categoryRepository.delete(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getCategoriesByType(OperationType type) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getOperationType() == type)
                .toList();
    }

    @Override
    public boolean categoryExists(UUID id) {
        return categoryRepository.exists(id);
    }
}