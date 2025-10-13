package moscowzoo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import moscowzoo.domain.entities.Animal;
import moscowzoo.domain.entities.Rabbit;
import moscowzoo.domain.entities.Table;
import moscowzoo.domain.entities.Tiger;
import moscowzoo.domain.enums.InventoryType;
import moscowzoo.domain.interfaces.Inventory;
import moscowzoo.service.implementations.CreationRequest;
import moscowzoo.service.implementations.InventoryFactory;
import moscowzoo.service.implementations.MoscowVetClinic;
import moscowzoo.service.implementations.MoscowZooService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MoscowZooServiceTests {

    @Mock
    private MoscowVetClinic vetClinic;

    @Mock
    private InventoryFactory inventoryFactory;

    private MoscowZooService zooService;

    @BeforeEach
    void setUp() {
        zooService = new MoscowZooService(vetClinic, inventoryFactory);
    }

    @Test
    @DisplayName("Should add healthy animal successfully")
    void shouldAddHealthyAnimal() {
        Rabbit rabbit = new Rabbit(1, "Bunny", 0.5, 7);
        when(vetClinic.isHealthy(rabbit)).thenReturn(true);

        boolean result = zooService.addAnimal(rabbit);

        assertTrue(result);
        List<Animal> animals = zooService.getAnimalsForContactZoo();
        List<Inventory> inventories = zooService.getInventories();
        assertEquals(1, animals.size());
        assertEquals(1, inventories.size());
        assertEquals("Bunny", animals.get(0).getName());
    }

    @Test
    @DisplayName("Should not add unhealthy animal")
    void shouldNotAddUnhealthyAnimal() {
        Tiger tiger = new Tiger(1, "Sher Khan", 5.0);
        when(vetClinic.isHealthy(tiger)).thenReturn(false);

        boolean result = zooService.addAnimal(tiger);

        assertFalse(result);
        List<Animal> animals = zooService.getAnimalsForContactZoo();
        List<Inventory> inventories = zooService.getInventories();
        assertTrue(animals.isEmpty());
        assertTrue(inventories.isEmpty());
    }

    @Test
    @DisplayName("Should add animal from request successfully")
    void shouldAddAnimalFromRequest() {
        CreationRequest request = new CreationRequest(
            InventoryType.RABBIT, "Bunny", 0.5, 7
        );
        Rabbit rabbit = new Rabbit(1, "Bunny", 0.5, 7);

        when(inventoryFactory.createItem(request)).thenReturn(rabbit);
        when(vetClinic.isHealthy(rabbit)).thenReturn(true);

        boolean result = zooService.addAnimalFromRequest(request);

        assertTrue(result);
        assertEquals(1, zooService.getAnimals().size());
    }

    @Test
    @DisplayName("Should add thing from request successfully")
    void shouldAddThingFromRequest() {
        CreationRequest request = new CreationRequest(
            InventoryType.TABLE, "Office Table", null, null
        );
        Table table = new Table(1, "Office Table");

        when(inventoryFactory.createItem(request)).thenReturn(table);

        zooService.addThingFromRequest(request);

        List<Inventory> inventories = zooService.getInventories();
        assertEquals(1, inventories.size());
        assertEquals("Office Table", inventories.get(0).getName());
    }

    @Test
    @DisplayName("Should calculate total food correctly")
    void shouldCalculateTotalFood() {
        when(vetClinic.isHealthy(any(Animal.class))).thenReturn(true);

        Rabbit rabbit = new Rabbit(1, "Bunny1", 0.5, 7);
        zooService.addAnimal(rabbit);

        Rabbit rabbit2 = new Rabbit(2, "Bunny2", 0.7, 8);
        zooService.addAnimal(rabbit2);

        Tiger tiger = new Tiger(3, "Sher Khan", 5.0);
        zooService.addAnimal(tiger);

        double totalFood = zooService.calculateTotalFood();
        assertEquals(6.2, totalFood, 0.001); // 0.5 + 0.7 + 5.0
    }

    @Test
    @DisplayName("Should return animals for contact zoo")
    void shouldReturnAnimalsForContactZoo() {
        when(vetClinic.isHealthy(any(Animal.class))).thenReturn(true);

        Rabbit contactRabbit = new Rabbit(1, "Friendly Bunny", 0.5, 8);
        zooService.addAnimal(contactRabbit);

        Rabbit nonContactRabbit = new Rabbit(2, "Shy Bunny", 0.5, 3);
        zooService.addAnimal(nonContactRabbit);

        Tiger tiger = new Tiger(3, "Sher Khan", 5.0);
        zooService.addAnimal(tiger);

        List<Animal> contactAnimals = zooService.getAnimalsForContactZoo();

        assertEquals(1, contactAnimals.size());
        assertEquals("Friendly Bunny", contactAnimals.get(0).getName());
        assertTrue(contactAnimals.get(0).canBeInContactZoo());
    }

    @Test
    @DisplayName("Should return empty list when no animals for contact zoo")
    void shouldReturnEmptyListWhenNoContactAnimals() {
        Rabbit shyRabbit = new Rabbit(1, "Shy Bunny", 0.5, 3);
        Tiger tiger = new Tiger(2, "Sher Khan", 5.0);

        when(vetClinic.isHealthy(any(Animal.class))).thenReturn(true);

        zooService.addAnimal(shyRabbit);
        zooService.addAnimal(tiger);

        List<Animal> contactAnimals = zooService.getAnimalsForContactZoo();

        assertTrue(contactAnimals.isEmpty());
    }

    @Test
    @DisplayName("Should return all inventories")
    void shouldReturnAllInventories() {
        Rabbit rabbit = new Rabbit(1, "Bunny", 0.5, 7);
        Table table = new Table(2, "Office Table");

        when(vetClinic.isHealthy(rabbit)).thenReturn(true);

        zooService.addAnimal(rabbit);
        zooService.addThing(table);

        List<Inventory> inventories = zooService.getInventories();

        assertEquals(2, inventories.size());
        assertTrue(inventories.stream().anyMatch(i -> i.getName().equals("Bunny")));
        assertTrue(inventories.stream().anyMatch(i -> i.getName().equals("Office Table")));
    }
}