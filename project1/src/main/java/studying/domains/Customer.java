package studying.domains;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Класс, представляющий покупателя.
 * Содержить физические данные и Iq
 */
@Getter
@ToString
public class Customer {
    private final String name;

    private final int legPower;

    private final int handPower;

    private final int iq;

    @Setter
    private Car car;

    /**
     * \Конструктор Покупателя
     * @param name имя покпателя
     * @param legPower сила ног покупателя
     * @param handPower сила рук покупателя
     * @param iq IQ покупателя
     */
    public Customer(String name, int legPower, int handPower, int iq) {
        this.name = name;
        this.legPower = legPower;
        this.handPower = handPower;
        this.iq = iq;
    }
}
