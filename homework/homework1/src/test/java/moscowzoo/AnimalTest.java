package moscowzoo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import moscowzoo.domain.entities.Computer;
import moscowzoo.domain.entities.Rabbit;
import moscowzoo.domain.entities.Table;
import moscowzoo.domain.entities.Tiger;
import moscowzoo.domain.entities.Wolf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnimalTest {

    @Test
    @DisplayName("Herbivore animal should be contactable when kindness > 5")
    void herbivoreShouldBeContactableWhenKindnessHigh() {
        Rabbit friendlyRabbit = new Rabbit(1, "Friendly", 0.5, 8);
        Rabbit shyRabbit = new Rabbit(2, "Shy", 0.5, 3);

        assertTrue(friendlyRabbit.canBeInContactZoo());
        assertFalse(shyRabbit.canBeInContactZoo());
    }

    @Test
    @DisplayName("Predator animal should not be contactable")
    void predatorShouldNotBeContactable() {
        Tiger tiger = new Tiger(1, "Sher Khan", 5.0);
        Wolf wolf = new Wolf(2, "Grey Wolf", 3.0);

        assertFalse(tiger.canBeInContactZoo());
        assertFalse(wolf.canBeInContactZoo());
    }

    @Test
    @DisplayName("Animals should have correct properties")
    void animalsShouldHaveCorrectProperties() {
        Rabbit rabbit = new Rabbit(1, "Bunny", 0.5, 7);

        assertEquals("Bunny", rabbit.getName());
        assertEquals(1, rabbit.getNumber());
        assertEquals(0.5, rabbit.getDailyFood(), 0.001);
        assertEquals(7, rabbit.getKindnessLevel());

        Tiger tiger = new Tiger(2, "Sher Khan", 5.0);

        assertEquals("Sher Khan", tiger.getName());
        assertEquals(2, tiger.getNumber());
        assertEquals(5.0, tiger.getDailyFood(), 0.001);
    }

    @Test
    @DisplayName("Things should have correct properties")
    void thingsShouldHaveCorrectProperties() {
        Table table = new Table(1, "Office Table");
        Computer computer = new Computer(2, "Workstation");

        assertEquals("Office Table", table.getName());
        assertEquals(1, table.getNumber());

        assertEquals("Workstation", computer.getName());
        assertEquals(2, computer.getNumber());
    }

    @Test
    @DisplayName("Should set number for inventory items")
    void shouldSetNumberForInventoryItems() {
        Rabbit rabbit = new Rabbit(1, "Bunny", 0.5, 7);
        Table table = new Table(2, "Office Table");

        rabbit.setNumber(10);
        table.setNumber(20);

        assertEquals(10, rabbit.getNumber());
        assertEquals(20, table.getNumber());
    }
}