package hsebank.finance.infrastructure.data.cache;

import hsebank.finance.core.domain.interfaces.IEntity;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RepositoryProxy<T extends IEntity> implements IRepository<T> {
    private final IRepository<T> realRepository;
    private final ConcurrentHashMap<UUID, T> cache = new ConcurrentHashMap<>();
    private int cacheHits = 0;
    private int cacheMisses = 0;
    private boolean allCached = false;

    public RepositoryProxy(IRepository<T> realRepository) {
        this.realRepository = realRepository;
        log.info("RepositoryProxy initialized for: {}", realRepository.getClass().getSimpleName());
    }

    @Override
    public T save(T entity) {
        log.debug("Saving entity: {}", entity.getId());
        T saved = realRepository.save(entity);
        cache.put(saved.getId(), saved);
        return saved;
    }

    @Override
    public Optional<T> findById(UUID id) {
        T cached = cache.get(id);
        if (cached != null) {
            cacheHits++;
            log.debug("Cache HIT for ID: {}", id);
            return Optional.of(cached);
        }

        cacheMisses++;
        log.debug("Cache MISS for ID: {}", id);
        Optional<T> result = realRepository.findById(id);

        result.ifPresent(entity -> {
            cache.put(entity.getId(), entity);
            log.debug("Cached entity: {}", entity.getId());
        });

        return result;
    }

    @Override
    public List<T> findAll() {
        if (allCached && !cache.isEmpty()) {
            cacheHits++;
            log.debug("Returning ALL from cache");
            return List.copyOf(cache.values());
        }

        cacheMisses++;
        log.debug("Loading ALL from real repository");
        List<T> allEntities = realRepository.findAll();

        cache.clear();
        allEntities.forEach(entity -> cache.put(entity.getId(), entity));
        allCached = true;

        log.debug("Cached {} entities", cache.size());
        return allEntities;
    }

    @Override
    public void delete(UUID id) {
        log.debug("Deleting entity: {}", id);
        realRepository.delete(id);
        cache.remove(id);
        allCached = false;
    }

    @Override
    public boolean exists(UUID id) {
        if (cache.containsKey(id)) {
            cacheHits++;
            return true;
        }

        cacheMisses++;
        return realRepository.exists(id);
    }

    public CacheStats getCacheStats() {
        return new CacheStats(
                cacheHits,
                cacheMisses,
                cache.size(),
                getCacheHitRatio()
        );
    }

    public double getCacheHitRatio() {
        int total = cacheHits + cacheMisses;
        return total > 0 ? (double) cacheHits / total : 0.0;
    }

    public void clearCache() {
        log.info("Clearing cache. Had {} entities", cache.size());
        cache.clear();
        cacheHits = 0;
        cacheMisses = 0;
        allCached = false;
    }

    public static class CacheStats {
        public final int hits;
        public final int misses;
        public final int size;
        public final double hitRatio;

        public CacheStats(int hits, int misses, int size, double hitRatio) {
            this.hits = hits;
            this.misses = misses;
            this.size = size;
            this.hitRatio = hitRatio;
        }
    }
}