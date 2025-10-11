package studying;

import studying.domains.Customer;
import studying.factories.HandCarFactory;
import studying.factories.LevitationCarFactory;
import studying.factories.PedalCarFactory;
import studying.params.EmptyEngineParams;
import studying.params.PedalEngineParams;
import studying.services.CarService;
import studying.services.CustomerStorage;
import studying.services.HseCarService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KpoApplicationTests {

	@Autowired
	private CarService carService;

	@Autowired
	private CustomerStorage customerStorage;

	@Autowired
	private HseCarService hseCarService;

	@Autowired
	private PedalCarFactory pedalCarFactory;

	@Autowired
	private HandCarFactory handCarFactory;

    @Autowired
    private LevitationCarFactory levitationCarFactory;

	@Test
	@DisplayName("Тест загрузки контекста")
	void contextLoads() {
		Assertions.assertNotNull(carService);
		Assertions.assertNotNull(customerStorage);
		Assertions.assertNotNull(hseCarService);
        Assertions.assertNotNull(levitationCarFactory);
        Assertions.assertNotNull(handCarFactory);
        Assertions.assertNotNull(pedalCarFactory);
	}

	@Test
	@DisplayName("Тест загрузки контекста")
	void hseCarServiceTest() {
		customerStorage.addCustomer(new Customer("Ivan1",6,4, 200));
		customerStorage.addCustomer(new Customer("Maksim",4,6, 100));
		customerStorage.addCustomer(new Customer("Petya",6,6, 200));
		customerStorage.addCustomer(new Customer("Nikita",4,4, 100));

        customerStorage.addCustomer(new Customer("Einstein",4,4, 350));
		
		carService.addCar(pedalCarFactory, new PedalEngineParams(6));
		carService.addCar(pedalCarFactory, new PedalEngineParams(6));

		carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);
		carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);

        carService.addCar(levitationCarFactory, EmptyEngineParams.DEFAULT);

		customerStorage.getCustomers().stream().map(Customer::toString).forEach(System.out::println);

		hseCarService.sellCars();

		customerStorage.getCustomers().stream().map(Customer::toString).forEach(System.out::println);
	}

}
