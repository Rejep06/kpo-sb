package studying.domains;

import lombok.Getter;
import lombok.ToString;
import studying.interfaces.IEngine;

/**
 * Класс педальный двигател
 */
@ToString
@Getter
public class PedalEngine implements IEngine {
    private final int size;

    /**
     * Проверка на совместимость с поукпателям
     * @param customer - покупатель для проверки совместимости
     * @return True если legPower>5 инае false
     */
    @Override
    public boolean isCompatible(Customer customer) {
        return customer.getLegPower() > 5;
    }

    public PedalEngine(int size) {
        this.size = size;
    }
}
