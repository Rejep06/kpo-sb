package HSEBank.finance.Infrastructure.config;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import HSEBank.finance.Infrastructure.data.cache.RepositoryProxy;
import HSEBank.finance.Infrastructure.data.repositories.BankAccountRepository;
import HSEBank.finance.Infrastructure.data.repositories.CategoryRepository;
import HSEBank.finance.Infrastructure.data.repositories.OperationRepository;
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