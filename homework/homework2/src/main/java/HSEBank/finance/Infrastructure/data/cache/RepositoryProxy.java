package HSEBank.finance.Infrastructure.data.cache;

import HSEBank.finance.Core.domain.interfaces.IEntity;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RepositoryProxy<T extends IEntity> implements IRepository<T> {
    private final IRepository<T> realRepository;
    private final Map<UUID, T> cache = new ConcurrentHashMap<>();
    private int cacheHits = 0;
    private int cacheMisses = 0;

    public RepositoryProxy(IRepository<T> realRepository) {
        this.realRepository = realRepository;
    }

    @Override
    public T save(T entity) {
        T saved = realRepository.save(entity);
        cache.put(saved.getId(), saved);
        return saved;
    }

    @Override
    public Optional<T> findById(UUID id) {
        T entity = cache.get(id);
        if (entity != null) {
            cacheHits++;
            return Optional.of(entity);
        }

        cacheMisses++;
        Optional<T> result = realRepository.findById(id);
        result.ifPresent(e -> cache.put(e.getId(), e));
        return result;
    }

    @Override
    public List<T> findAll() {
        List<T> allEntities = realRepository.findAll();
        cache.clear();
        allEntities.forEach(entity -> cache.put(entity.getId(), entity));
        return allEntities;
    }

    @Override
    public void delete(UUID id) {
        realRepository.delete(id);
        cache.remove(id);
    }

    @Override
    public boolean exists(UUID id) {
        return cache.containsKey(id) || realRepository.exists(id);
    }

    public double getCacheHitRatio() {
        int total = cacheHits + cacheMisses;
        return total > 0 ? (double) cacheHits / total : 0.0;
    }

    public void clearCache() {
        cache.clear();
        cacheHits = 0;
        cacheMisses = 0;
    }

}