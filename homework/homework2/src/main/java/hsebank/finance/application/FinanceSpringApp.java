package hsebank.finance.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "hsebank.finance")
public class FinanceSpringApp {

    public static void main(String[] args) {
        try {
            log.info("Starting hsebank Finance Application...");
            SpringApplication.run(FinanceSpringApp.class, args);
            log.info("hsebank Finance Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start application", e);
            System.exit(1);
        }
    }
}