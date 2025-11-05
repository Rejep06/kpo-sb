package HSEBank.finance.Infrastructure.data.repositories;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

public class BankAccountRepository implements IRepository<BankAccount> {
    private final Map<UUID, BankAccount> storage = new ConcurrentHashMap<>();

    @Override
    public BankAccount save(BankAccount entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<BankAccount> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID id) {
        storage.remove(id);
    }

    @Override
    public boolean exists(UUID id) {
        return storage.containsKey(id);
    }
}
