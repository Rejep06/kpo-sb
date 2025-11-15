package hsebank.finance.application;

import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.enums.OperationType;
import hsebank.finance.core.domain.interfaces.ICategoryService;
import hsebank.finance.core.patterns.commands.UpdateCategoryCommand;
import hsebank.finance.core.patterns.decorator.TimingDecorator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryManager {
    private final ICategoryService categoryService;
    private final CLIMenuService menuService;
    private final InputValidator inputValidator;

    public void createCategory() {
        try {
            OperationType type = inputValidator.getValidatedOperationType("Select category type:");
            String name = menuService.getInput("Enter category name: ");

            Category category = categoryService.createCategory(type, name);
            menuService.showSuccess("Category created: " + category.getName() + " (ID: " + category.getId() + ")");

        } catch (Exception e) {
            menuService.showError("Category creation failed: " + e.getMessage());
        }
    }

    public void showAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                menuService.showMessage(" No categories found");
                return;
            }

            menuService.showMessage("\n CATEGORIES LIST:");
            menuService.showMessage("┌────────────────────────────────────┬────────────┬────────────────────┐");
            menuService.showMessage("│ ID                                 │ Type       │ Name               │");
            menuService.showMessage("├────────────────────────────────────┼────────────┼────────────────────┤");

            for (Category category : categories) {
                String typeEmoji = category.getOperationType() == OperationType.INCOME ? "💰" : "💸";
                String line = String.format("│ %-34s │ %-4s%-6s │ %-18s │",
                        category.getId(),
                        typeEmoji,
                        category.getOperationType(),
                        category.getName());
                menuService.showMessage(line);
            }
            menuService.showMessage("└────────────────────────────────────┴────────────┴────────────────────┘");

        } catch (Exception e) {
            menuService.showError("Failed to load categories: " + e.getMessage());
        }
    }

    public void findCategoryById() {
        try {
            String id = inputValidator.getValidatedUUID("Enter category ID: ");
            Category category = categoryService.getCategory(UUID.fromString(id));

            menuService.showMessage("\n CATEGORY DETAILS:");
            menuService.showMessage("ID: " + category.getId());
            menuService.showMessage("Type: " + category.getOperationType());
            menuService.showMessage("Name: " + category.getName());

        } catch (Exception e) {
            menuService.showError("Category not found: " + e.getMessage());
        }
    }

    public void updateCategory() {
        try {
            String id = inputValidator.getValidatedUUID("Enter category ID to update: ");
            OperationType type = inputValidator.getValidatedOperationType("Select new category type:");
            String name = menuService.getInput("Enter new category name: ");

            var updateCmd = new UpdateCategoryCommand(categoryService, UUID.fromString(id), type, name);
            new TimingDecorator(updateCmd).execute();

            menuService.showSuccess("Category updated successfully");

        } catch (Exception e) {
            menuService.showError("Category update failed: " + e.getMessage());
        }
    }

    public void deleteCategory() {
        try {
            String id = inputValidator.getValidatedUUID("Enter category ID to delete: ");
            categoryService.deleteCategory(UUID.fromString(id));
            menuService.showSuccess("Category deleted successfully");

        } catch (Exception e) {
            menuService.showError("Category deletion failed: " + e.getMessage());
        }
    }
}