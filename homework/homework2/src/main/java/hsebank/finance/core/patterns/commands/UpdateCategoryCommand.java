package hsebank.finance.core.patterns.commands;

import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.enums.OperationType;
import hsebank.finance.core.domain.interfaces.ICategoryService;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UpdateCategoryCommand extends BaseCommand {
    private final ICategoryService categoryService;
    private final UUID categoryId;
    private final OperationType newType;
    private final String newName;
    private final OperationType oldType;
    private final String oldName;
    private Category updatedCategory;

    public UpdateCategoryCommand(ICategoryService categoryService, UUID categoryId,
                                 OperationType newType, String newName) {
        super("Update category: " + categoryId);
        this.categoryService = categoryService;
        this.categoryId = categoryId;
        this.newType = newType;
        this.newName = newName;

        Category oldCategory = categoryService.getCategory(categoryId);
        this.oldType = oldCategory.getOperationType();
        this.oldName = oldCategory.getName();
    }

    @Override
    public void execute() {
        updatedCategory = categoryService.updateCategory(categoryId, newType, newName);
    }

    @Override
    public void undo() {
        categoryService.updateCategory(categoryId, oldType, oldName);
    }
}