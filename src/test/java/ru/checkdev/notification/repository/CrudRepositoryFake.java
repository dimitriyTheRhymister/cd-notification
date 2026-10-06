package ru.checkdev.notification.repository;

import org.springframework.data.repository.CrudRepository;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Собственная реализация CrudRepository для тестов,
 * работающая с javax.persistence.Id (Spring Boot 2.7).
 */
public abstract class CrudRepositoryFake<T, ID> implements CrudRepository<T, ID> {

    protected final Map<ID, T> memory = new ConcurrentHashMap<>();

    @Override
    public <S extends T> S save(S entity) {
        ID id = getId(entity);
        memory.put(id, entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(memory.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return memory.containsKey(id);
    }

    @Override
    public Iterable<T> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        List<T> result = new ArrayList<>();
        for (ID id : ids) {
            if (memory.containsKey(id)) {
                result.add(memory.get(id));
            }
        }
        return result;
    }

    @Override
    public long count() {
        return memory.size();
    }

    @Override
    public void deleteById(ID id) {
        memory.remove(id);
    }

    @Override
    public void delete(T entity) {
        ID id = getId(entity);
        memory.remove(id);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        memory.clear();
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        for (ID id : ids) {
            memory.remove(id);
        }
    }

    @SuppressWarnings("unchecked")
    private ID getId(T entity) {
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return (ID) field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot access @Id field", e);
                }
            }
        }
        throw new IllegalStateException("Entity does not have @Id field (javax.persistence.Id)");
    }
}