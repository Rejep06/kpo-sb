package hse.kpo.utils;

import hse.kpo.controllers.CustomerResponse;
import hse.kpo.domains.Customer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerResponseUtils {
    public CustomerResponse convertToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getLegPower(),
                customer.getHandPower(),
                customer.getIq()
        );
    }
}
