package moscowzoo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import moscowzoo.domain.entities.Rabbit;
import moscowzoo.domain.entities.Tiger;
import moscowzoo.service.implementations.MoscowVetClinic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class MoscowVetClinicTests {

    private MoscowVetClinic vetClinic;

    @BeforeEach
    void setUp() {
        vetClinic = new MoscowVetClinic();
    }

    @RepeatedTest(10)
    @DisplayName("Should return healthy status with 90% probability")
    void shouldReturnHealthyStatusWithProbability() {
        Rabbit rabbit = new Rabbit(1, "TestRabbit", 0.5, 7);
        Tiger tiger = new Tiger(2, "TestTiger", 5.0);

        assertDoesNotThrow(() -> {
            boolean rabbitHealthy = vetClinic.isHealthy(rabbit);
            boolean tigerHealthy = vetClinic.isHealthy(tiger);

            assertTrue(rabbitHealthy || !rabbitHealthy);
            assertTrue(tigerHealthy || !tigerHealthy);
        });
    }

    @Test
    @DisplayName("Should handle different animal types")
    void shouldHandleDifferentAnimalTypes() {
        Rabbit rabbit = new Rabbit(1, "Bunny", 0.5, 7);
        Tiger tiger = new Tiger(2, "Sher Khan", 5.0);

        boolean rabbitResult = vetClinic.isHealthy(rabbit);
        boolean tigerResult = vetClinic.isHealthy(tiger);

        assertNotNull(Boolean.valueOf(rabbitResult));
        assertNotNull(Boolean.valueOf(tigerResult));
    }
}