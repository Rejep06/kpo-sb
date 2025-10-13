package moscowzoo.service.interfaces;

import java.util.List;
import moscowzoo.domain.entities.Animal;
import moscowzoo.domain.interfaces.Inventory;
import moscowzoo.service.implementations.CreationRequest;


/**
 * Interface ZooService.
 */
public interface ZooService {
    boolean addAnimal(Animal animal);

    void addThing(Inventory thing);

    double calculateTotalFood();

    List<Animal> getAnimalsForContactZoo();

    List<Inventory> getInventories();

    boolean addAnimalFromRequest(CreationRequest request);

    void addThingFromRequest(CreationRequest request);
}
