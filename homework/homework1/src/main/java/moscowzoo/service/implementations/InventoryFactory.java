package moscowzoo.service.implementations;

import moscowzoo.domain.entities.Computer;
import moscowzoo.domain.entities.Monkey;
import moscowzoo.domain.entities.Rabbit;
import moscowzoo.domain.entities.Table;
import moscowzoo.domain.entities.Tiger;
import moscowzoo.domain.entities.Wolf;
import moscowzoo.domain.enums.InventoryType;
import moscowzoo.domain.interfaces.Inventory;
import org.springframework.stereotype.Component;

/**
 * InventoryFactory for creating inventory from creation request.
 */
@Component
public class InventoryFactory {
    private int inventoryCounter = 0;

    /**
     * Constructor InventoryFactory.
     *
     * @param request creation request for inventory
     */
    public Inventory createItem(CreationRequest request) {
        InventoryType itemType = request.getInventoryType();
        int inventoryNumber = ++inventoryCounter;
        switch (itemType) {
            case (InventoryType.RABBIT):
                validateHerbivoreParams(request);
                return new Rabbit(inventoryNumber, request.getName(), request.getFood(), request.getKindnessLevel());
            case (InventoryType.MONKEY):
                validateHerbivoreParams(request);
                return new Monkey(inventoryNumber, request.getName(), request.getFood(), request.getKindnessLevel());
            case (InventoryType.TIGER):
                validateAnimalParams(request);
                return new Tiger(inventoryNumber, request.getName(), request.getFood());
            case (InventoryType.WOLF):
                validateAnimalParams(request);
                return new Wolf(inventoryNumber, request.getName(), request.getFood());
            case (InventoryType.TABLE):
                return new Table(inventoryNumber, request.getName());
            case (InventoryType.COMPUTER):
                return new Computer(inventoryNumber, request.getName());
            default:
                throw new IllegalArgumentException("Неизвестный тип: " + itemType);
        }
    }

    private void validateHerbivoreParams(CreationRequest request) {
        validateAnimalParams(request);
        if (request.getKindnessLevel() == null) {
            throw new IllegalArgumentException("For herbivores, kindness must be specified");
        }
        if (request.getKindnessLevel() <= 0) {
            throw new IllegalArgumentException("Animal kindness must be positive");
        }
    }

    private void validateAnimalParams(CreationRequest request) {
        if (request.getFood() == null) {
            throw new IllegalArgumentException("Animals are required to indicate food consumption");
        }
        if (request.getFood() <= 0) {
            throw new IllegalArgumentException("Food consumption should be positive");
        }
    }

    public int getCurrentInventoryNumber() {
        return inventoryCounter;
    }
}
