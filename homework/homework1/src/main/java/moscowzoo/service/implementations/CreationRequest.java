package moscowzoo.service.implementations;

import lombok.Getter;
import moscowzoo.domain.enums.InventoryType;

/**
 * Class CreationRequest for create request objects of inventory interface.
 */
@Getter
public class CreationRequest {
    private InventoryType inventoryType;
    private String name;
    private Double food;
    private Integer kindnessLevel;

    /**
     * CreationRequest constructor.
     *
     * @param inventoryType type of inventory
     * @param name name of inventory
     * @param food daily food for animals
     * @param kindnessLevel kindness level for herbivore animals
     */
    public CreationRequest(InventoryType inventoryType, String name, Double food, Integer kindnessLevel) {
        this.inventoryType = inventoryType;
        this.name = name;
        this.food = food;
        this.kindnessLevel = kindnessLevel;
    }
}
