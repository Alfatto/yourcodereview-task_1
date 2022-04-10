package alfatto_task1_junior.redis;


import java.util.Set;

public interface RedisCache {
    void set(String key, String value);
    String get(String key);
    int del(String[] keys);
    Set<String> keys(String pattern);
}
