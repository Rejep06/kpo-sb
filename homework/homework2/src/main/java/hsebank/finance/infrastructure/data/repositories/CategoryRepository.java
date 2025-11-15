package hsebank.finance.infrastructure.data.repositories;

import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CategoryRepository implements IRepository<Category> {
    private final Map<UUID, Category> storage = new ConcurrentHashMap<>();

    @Override
    public Category save(Category entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Category> findAll() {
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
