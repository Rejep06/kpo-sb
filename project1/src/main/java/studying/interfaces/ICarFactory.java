package studying.interfaces;

import studying.domains.Car;
import studying.params.EmptyEngineParams;

public interface ICarFactory<TParams> {
    Car createCar(TParams carParams, int carNumber);
}
