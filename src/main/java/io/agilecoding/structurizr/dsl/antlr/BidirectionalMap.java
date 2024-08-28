package io.agilecoding.structurizr.dsl.antlr;

import java.util.HashMap;
import java.util.Map;

class BidirectionalMap<K, V> {
    private final Map<K, V> keyToValueMap = new HashMap<>();
    private final Map<V, K> valueToKeyMap = new HashMap<>();

    public void put(K key, V value) {
        if (keyToValueMap.containsKey(key)) {
            throw new IllegalArgumentException("key " + key + " is already used by " + keyToValueMap.get(key));
        }
        if (valueToKeyMap.containsKey(value)) {
            throw new IllegalArgumentException("value " + value + " already exists with key " + valueToKeyMap.get(value));
        }
        keyToValueMap.put(key, value);
        valueToKeyMap.put(value, key);
    }

    public V getValueByKey(K key) {
        return keyToValueMap.get(key);
    }

    public K getKeyByValue(V value) {
        return valueToKeyMap.get(value);
    }
}