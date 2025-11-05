package HSEBank.finance.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackages = "HSEBank.finance")
public class FinanceSpringApp {

    public static void main(String[] args) {
        try {
            log.info("Starting HSEBank Finance Application...");
            SpringApplication.run(FinanceSpringApp.class, args);
            log.info("HSEBank Finance Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start application", e);
            System.exit(1);
        }
    }
}