package moscowzoo.service.implementations;

import moscowzoo.domain.entities.Animal;
import moscowzoo.service.interfaces.VetClinic;
import org.springframework.stereotype.Component;

/**
 * Class MoscowVetClinic.
 */
@Component
public class MoscowVetClinic implements VetClinic {
    @Override
    public boolean isHealthy(Animal animal) {
        return Math.random() > 0.1;
    }
}
