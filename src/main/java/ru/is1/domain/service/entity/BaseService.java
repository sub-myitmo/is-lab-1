package ru.is1.domain.service.entity;

import java.util.List;
import java.util.Optional;

public interface BaseService<T> {
    Optional<T> getEntityById(Long id);
    boolean deleteEntity(Long id);
    T updateEntity(T entity);
    long getTotalEntitiesCount();
    T createEntity(T entity);
    List<T> getEntitiesPaginated(int first, int size, String field, String direction);

}
