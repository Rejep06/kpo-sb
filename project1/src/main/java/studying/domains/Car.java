package studying.domains;

import lombok.Getter;
import lombok.ToString;
import studying.interfaces.IEngine;

/**
 * Класс, представляющий автомобиль.
 * Содержит информацию о VIN и двигателе автомобиля.
 */
@ToString
public class Car {

    private IEngine engine;

    @Getter
    private int VIN;

    /**
     * Конструктор автомобиля.
     *
     * @param VIN уникальный идентификатор автомобиля
     * @param engine двигатель автомобиля
     */
    public Car(int VIN, IEngine engine) {
        this.VIN = VIN;
        this.engine = engine;
    }

    /**
     * Проверяет совместимость автомобиля с покупателем.
     * Совместимость определяется двигателем автомобиля.
     *
     * @param customer покупатель для проверки совместимости
     * @return true если автомобиль совместим с покупателем, иначе false
     */
    public boolean isCompatible(Customer customer) {
        return this.engine.isCompatible(customer); // внутри метода просто вызываем соответствующий метод двигателя
    }
}
