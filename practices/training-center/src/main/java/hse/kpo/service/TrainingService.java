package hse.kpo.service;

import hse.kpo.controllers.CustomerResponse;
import hse.kpo.kafka.TrainingCompletedEvent;
import hse.kpo.repositories.CustomerRepository;
import hse.kpo.utils.CustomerResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TrainingService {

    private final KafkaTemplate<String, TrainingCompletedEvent> kafkaTemplate;
    private final CustomerRepository repository;

    public String trainCustomer(int customerId, String trainType) {
        var customer = repository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        switch (trainType) {
            case "handPower" -> customer.setHandPower(customer.getHandPower() + 1);
            case "legPower" -> customer.setLegPower(customer.getLegPower() + 1);
            case "iq" -> customer.setIq(customer.getIq() + 1);
            default -> throw new RuntimeException("bad trainType: " + trainType);
        }

        // Отправляем обновлённые данные обратно в Sales Service
        TrainingCompletedEvent trainingCompletedEvent = new TrainingCompletedEvent(
                customerId, trainType
        );

        kafkaTemplate.send("training-updates", trainingCompletedEvent);

        return "Тренировка завершена! Параметры обновлены";
    }

    public List<CustomerResponse> getAllCustomers() {
        return repository.findAll().stream()
                .map(CustomerResponseUtils::convertToResponse)
                .collect(Collectors.toList());
    }


}
