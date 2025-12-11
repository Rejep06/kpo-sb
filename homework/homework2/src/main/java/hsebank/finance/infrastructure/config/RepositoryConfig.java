package hsebank.finance.infrastructure.config;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.interfaces.IRepository;
import hsebank.finance.infrastructure.data.cache.RepositoryProxy;
import hsebank.finance.infrastructure.data.repositories.BankAccountRepository;
import hsebank.finance.infrastructure.data.repositories.CategoryRepository;
import hsebank.finance.infrastructure.data.repositories.OperationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RepositoryConfig {

    @Bean
    @Primary
    public IRepository<BankAccount> bankAccountRepository() {
        BankAccountRepository realRepository = new BankAccountRepository();
        return new RepositoryProxy<>(realRepository);
    }

    @Bean
    @Primary
    public IRepository<Category> categoryRepository() {
        CategoryRepository realRepository = new CategoryRepository();
        return new RepositoryProxy<>(realRepository);
    }

    @Bean
    @Primary
    public IRepository<Operation> operationRepository() {
        OperationRepository realRepository = new OperationRepository();
        return new RepositoryProxy<>(realRepository);
    }
}