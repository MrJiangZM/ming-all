package com.ming.blog.cache;

import java.util.List;

public interface RemovableListCache<K, V> {
    void mutRemove(List<V> values);

    void mutRemove(K key, List<V> values);

    void remove(V value);

    void remove(K key, V value);
}
