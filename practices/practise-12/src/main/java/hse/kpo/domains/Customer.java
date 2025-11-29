package hse.kpo.domains;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.catamarans.Catamaran;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Класс, описывающий покупателя.
 */
@Getter
@Setter
@Entity
@Table(name = "customers")
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int legPower;

    @Column(nullable = false)
    private int handPower;

    @Column(nullable = false)
    private int iq;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "catamaran_id")
    private Catamaran catamaran;

    public Customer(String name, int legPower, int handPower, int iq) {
        this.name = name;
        this.legPower = legPower;
        this.handPower = handPower;
        this.iq = iq;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", legPower=" + legPower +
                ", handPower=" + handPower +
                ", iq=" + iq +
                ", cars=" + cars.stream().map(Car::getVin).toList().toString() +
                ", catamaran=" + catamaran +
                '}';
    }
}
