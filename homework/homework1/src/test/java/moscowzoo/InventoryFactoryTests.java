package moscowzoo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import moscowzoo.domain.entities.Computer;
import moscowzoo.domain.entities.Monkey;
import moscowzoo.domain.entities.Rabbit;
import moscowzoo.domain.entities.Table;
import moscowzoo.domain.entities.Tiger;
import moscowzoo.domain.entities.Wolf;
import moscowzoo.domain.enums.InventoryType;
import moscowzoo.domain.interfaces.Inventory;
import moscowzoo.service.implementations.CreationRequest;
import moscowzoo.service.implementations.InventoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


class InventoryFactoryTests {

    private InventoryFactory inventoryFactory;

    @BeforeEach
    void setUp() {
        inventoryFactory = new InventoryFactory();
    }

    @Test
    @DisplayName("Should create Rabbit with correct parameters")
    void shouldCreateRabbit() {
        CreationRequest request = new CreationRequest(
            InventoryType.RABBIT, "Bunny", 0.5, 7
        );

        Inventory result = inventoryFactory.createItem(request);

        assertNotNull(result);
        assertInstanceOf(Rabbit.class, result);
        assertEquals("Bunny", result.getName());
        assertEquals(1, result.getNumber());
        assertEquals(0.5, ((Rabbit) result).getDailyFood(), 0.001);
        assertEquals(7, ((Rabbit) result).getKindnessLevel());
    }

    @Test
    @DisplayName("Should create Tiger with correct parameters")
    void shouldCreateTiger() {
        CreationRequest request = new CreationRequest(
            InventoryType.TIGER, "Sher Khan", 5.0, null
        );

        Inventory result = inventoryFactory.createItem(request);

        assertNotNull(result);
        assertTrue(result instanceof Tiger);
        assertEquals("Sher Khan", result.getName());
        assertEquals(1, result.getNumber());
        assertEquals(5.0, ((Tiger) result).getDailyFood(), 0.001);
    }

    @Test
    @DisplayName("Should create Table with correct parameters")
    void shouldCreateTable() {
        CreationRequest request = new CreationRequest(
            InventoryType.TABLE, "Office Table", null, null
        );

        Inventory result = inventoryFactory.createItem(request);

        assertNotNull(result);
        assertTrue(result instanceof Table);
        assertEquals("Office Table", result.getName());
        assertEquals(1, result.getNumber());
    }

    @Test
    @DisplayName("Should create Computer with correct parameters")
    void shouldCreateComputer() {
        CreationRequest request = new CreationRequest(
            InventoryType.COMPUTER, "Workstation", null, null
        );

        Inventory result = inventoryFactory.createItem(request);

        assertNotNull(result);
        assertTrue(result instanceof Computer);
        assertEquals("Workstation", result.getName());
        assertEquals(1, result.getNumber());
    }

    @Test
    @DisplayName("Should throw exception when creating herbivore without kindness")
    void shouldThrowExceptionWhenHerbivoreWithoutKindness() {
        CreationRequest request = new CreationRequest(
            InventoryType.RABBIT, "Bunny", 0.5, null
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> inventoryFactory.createItem(request)
        );

        assertEquals("For herbivores, kindness must be specified", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating animal without food")
    void shouldThrowExceptionWhenAnimalWithoutFood() {
        CreationRequest request = new CreationRequest(
            InventoryType.TIGER, "Sher Khan", null, null
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> inventoryFactory.createItem(request)
        );

        assertEquals("Animals are required to indicate food consumption", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating animal with negative food")
    void shouldThrowExceptionWhenAnimalWithNegativeFood() {
        CreationRequest request = new CreationRequest(
            InventoryType.TIGER, "Sher Khan", -1.0, null
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> inventoryFactory.createItem(request)
        );

        assertEquals("Food consumption should be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating herbivore with negative kindness")
    void shouldThrowExceptionWhenHerbivoreWithNegativeKindness() {
        CreationRequest request = new CreationRequest(
            InventoryType.RABBIT, "Bunny", 0.5, -1
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> inventoryFactory.createItem(request)
        );

        assertEquals("Animal kindness must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Should increment inventory counter for each creation")
    void shouldIncrementInventoryCounter() {
        CreationRequest request1 = new CreationRequest(
            InventoryType.RABBIT, "Bunny1", 0.5, 7
        );
        CreationRequest request2 = new CreationRequest(
            InventoryType.TIGER, "Tiger1", 5.0, null
        );

        Inventory item1 = inventoryFactory.createItem(request1);
        Inventory item2 = inventoryFactory.createItem(request2);

        assertEquals(1, item1.getNumber());
        assertEquals(2, item2.getNumber());
        assertEquals(2, inventoryFactory.getCurrentInventoryNumber());
    }

    @ParameterizedTest
    @MethodSource("provideAnimalTypes")
    @DisplayName("Should create different animal types")
    void shouldCreateDifferentAnimalTypes(InventoryType type, Class<?> expectedClass) {
        Double food = type == InventoryType.TABLE || type == InventoryType.COMPUTER ? null : 1.0;
        Integer kindness = (type == InventoryType.RABBIT || type == InventoryType.MONKEY) ? 6 : null;

        CreationRequest request = new CreationRequest(type, "Test", food, kindness);

        Inventory result = inventoryFactory.createItem(request);

        assertNotNull(result);
        assertTrue(expectedClass.isInstance(result));
    }

    private static Stream<Arguments> provideAnimalTypes() {
        return Stream.of(
            Arguments.of(InventoryType.RABBIT, Rabbit.class),
            Arguments.of(InventoryType.MONKEY, Monkey.class),
            Arguments.of(InventoryType.TIGER, Tiger.class),
            Arguments.of(InventoryType.WOLF, Wolf.class),
            Arguments.of(InventoryType.TABLE, Table.class),
            Arguments.of(InventoryType.COMPUTER, Computer.class)
        );
    }
}