package moscowzoo.service.implementations;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import moscowzoo.domain.entities.Animal;
import moscowzoo.domain.interfaces.Inventory;
import moscowzoo.service.interfaces.VetClinic;
import moscowzoo.service.interfaces.ZooService;
import org.springframework.stereotype.Service;

/**
 * Class MoscowZooService.
 */
@Service
public class MoscowZooService implements ZooService {
    @Getter
    private final List<Animal> animals = new ArrayList<>();
    private final List<Inventory> inventories = new ArrayList<>();
    private final VetClinic vetClinic;
    private final InventoryFactory inventoryFactory;

    public MoscowZooService(VetClinic vetClinic, InventoryFactory inventoryFactory) {
        this.vetClinic = vetClinic;
        this.inventoryFactory = inventoryFactory;
    }

    @Override
    public boolean addAnimalFromRequest(CreationRequest request) {
        try {
            Inventory item = inventoryFactory.createItem(request);
            if (item instanceof Animal animal) {
                return addAnimal(animal);
            }
            return false;
        } catch (Exception e) {
            System.out.println("Ошибка создания животного: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void addThingFromRequest(CreationRequest request) {
        try {
            Inventory item = inventoryFactory.createItem(request);
            if (item instanceof moscowzoo.domain.entities.Thing) {
                addThing(item);
            }
        } catch (Exception e) {
            System.out.println("Ошибка создания вещи: " + e.getMessage());
        }
    }

    @Override
    public boolean addAnimal(Animal animal) {
        if (vetClinic.isHealthy(animal)) {
            animals.add(animal);
            inventories.add(animal);
            return true;
        }
        return false;
    }

    @Override
    public void addThing(Inventory thing) {
        inventories.add(thing);
    }

    @Override
    public double calculateTotalFood() {
        return animals.stream().mapToDouble(Animal::getDailyFood).sum();
    }

    @Override
    public List<Animal> getAnimalsForContactZoo() {
        return animals.stream().filter(Animal::canBeInContactZoo).toList();
    }

    @Override
    public List<Inventory> getInventories() {
        return inventories;
    }
}
