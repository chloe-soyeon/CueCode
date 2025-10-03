package kopo.userservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void set(String key, String value, long timeoutSeconds) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

