package hsebank.finance.core.domain.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRepository<T extends IEntity> {
    T save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    void delete(UUID id);

    boolean exists(UUID id);
}
