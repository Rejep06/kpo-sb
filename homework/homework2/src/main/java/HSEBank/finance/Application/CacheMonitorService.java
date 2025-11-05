package HSEBank.finance.Application;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import HSEBank.finance.Infrastructure.data.cache.RepositoryProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CacheMonitorService {

    private final IRepository<BankAccount> accountRepository;
    private final IRepository<Category> categoryRepository;
    private final IRepository<Operation> operationRepository;

    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();

        if (accountRepository instanceof RepositoryProxy) {
            RepositoryProxy.CacheStats accountStats = ((RepositoryProxy<BankAccount>) accountRepository).getCacheStats();
            stats.put("accounts", accountStats);
        }

        if (categoryRepository instanceof RepositoryProxy) {
            RepositoryProxy.CacheStats categoryStats = ((RepositoryProxy<Category>) categoryRepository).getCacheStats();
            stats.put("categories", categoryStats);
        }

        if (operationRepository instanceof RepositoryProxy) {
            RepositoryProxy.CacheStats operationStats = ((RepositoryProxy<Operation>) operationRepository).getCacheStats();
            stats.put("operations", operationStats);
        }

        return stats;
    }

    public void clearAllCaches() {
        if (accountRepository instanceof RepositoryProxy) {
            ((RepositoryProxy<BankAccount>) accountRepository).clearCache();
        }
        if (categoryRepository instanceof RepositoryProxy) {
            ((RepositoryProxy<Category>) categoryRepository).clearCache();
        }
        if (operationRepository instanceof RepositoryProxy) {
            ((RepositoryProxy<Operation>) operationRepository).clearCache();
        }
    }
}