package studying.domains;

import lombok.ToString;
import studying.interfaces.IEngine;

/**
 * Класс Левитирующего двигателя
 */
@ToString
public class LevitationEngine implements IEngine {
    /**
     * Проверяет совместимость с покупателям
     * @param customer - покупатель для проверки совместимости
     * @return True если iq>300 иначе false
     */
    @Override
    public boolean isCompatible(Customer customer){return customer.getIq()>300;}
}
