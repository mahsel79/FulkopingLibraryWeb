package se.fulkopinglibraryweb.service.interfaces;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheService<K, V> {
    Optional<V> get(K key);
    void put(K key, V value);
    void put(K key, V value, long timeout, TimeUnit unit);
    void remove(K key);
    void clear();
    boolean exists(K key);
    void warmUp();
}
