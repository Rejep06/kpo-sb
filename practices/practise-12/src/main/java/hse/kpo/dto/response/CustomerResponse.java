package hse.kpo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomerResponse {
    private String name;
    private int legPower;
    private int handPower;
    private int iq;
    private List<Integer> carVin;
    private Integer catamaranVin;
}