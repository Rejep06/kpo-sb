package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.ICategoryService;
import lombok.Getter;

import java.util.UUID;

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