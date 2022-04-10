package com.alfatto.task1_junior.redis;

import alfatto_task1_junior.redis.RedisCache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RedisCacheImpl implements RedisCache {
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public void set(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        String value = cache.get(key);
        return value == null ? "nil" : value;
    }

    @Override
    public int del(String[] keys) {
        int countDeleted = 0;

        for(String key: keys){
            String value = cache.remove(key);
            if(value != null){
                countDeleted++;
            }
        }

        return countDeleted;
    }

    @Override
    public Set<String> keys(String pattern) {
        pattern = pattern
                .replace("?", ".")
                .replace("*", ".*");

        Set<String> keys = new HashSet<>();
        for(String key : cache.keySet()){
            if(key.matches(pattern)){
                keys.add(key);
            }
        }

        return keys;
    }
}
