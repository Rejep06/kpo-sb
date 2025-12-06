package hse.kpo.controllers;

import java.util.List;
import hse.kpo.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = " Тренировочный центр", description = "Тренировка покупателей")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping("/train/{customerId}")
    public ResponseEntity<String> trainCustomer(@PathVariable int customerId,
                                                @RequestParam String trainType) {
        return ResponseEntity.ok(trainingService.trainCustomer(customerId, trainType));
    }

    @GetMapping
    @Operation(summary = "Получить всех клиентов")
    public List<CustomerResponse> getAllCustomers() {
        return trainingService.getAllCustomers();
    }
}
