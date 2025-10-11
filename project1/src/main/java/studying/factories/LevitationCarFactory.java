package studying.factories;

import lombok.ToString;
import org.springframework.stereotype.Component;
import studying.domains.Car;
import studying.domains.LevitationEngine;
import studying.interfaces.ICarFactory;
import studying.params.EmptyEngineParams;

@Component
@ToString
public class LevitationCarFactory implements ICarFactory<EmptyEngineParams> {
    @Override
    public Car createCar(EmptyEngineParams carParams, int carNumber) {
        var engine = new LevitationEngine();
        return new Car(carNumber, engine);
    }
}
