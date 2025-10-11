package studying.domains;

import lombok.ToString;
import studying.interfaces.IEngine;

/**
 * Класс ручного двигателя
 */
@ToString
public class HandEngine implements IEngine {
    /**
     * Провеяет совместимость двигателя и покпуателя
     * @param customer - покупатель для проверки совместимости
     * @return true елси handPower > 5 иначе false
     */
    @Override
    public boolean isCompatible(Customer customer) {
        return customer.getHandPower() > 5;
    }
}
