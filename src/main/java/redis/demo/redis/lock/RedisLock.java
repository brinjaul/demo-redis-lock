package redis.demo.redis.lock;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @ClassName: RedisLock
 * @Description: 此锁为集合锁，仅仅锁数据集合块  key  集合 id    value  数据块标识
 * @Author fjp
 * @Date 2020/11/30-13:42
 * @Version 1.0
 */
public class RedisLock extends AbstractLock {

    public RedisLock(String lockKey) {
        this.lockKey = lockKey;
    }

    private String lockKey;//objectName

    private int currentPart; //暂时先不用，考虑到后续通用使用uuid来代替


    public int getCurrentPart() {
        return currentPart;
    }

    public RedisLock setCurrentPart(int currentPart) {
        this.currentPart = currentPart;
        return this;
    }

    public String getLockKey() {
        return lockKey;
    }

    public RedisLock setLockKey(String lockKey, int currentPart) {
        this.lockKey = lockKey;
        return this;
    }

    private DefaultRedisScript script;

    private static RedisTemplate template;

    public DefaultRedisScript getScriptByTemplate(String path) {
        DefaultRedisScript defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(String.class);
//        defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));
        defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        this.script = defaultRedisScript;
        return defaultRedisScript;
    }

    public RedisTemplate initTemplate() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("127.0.0.1");
        config.setPort(6379);
        config.setDatabase(0);



        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        RedisTemplate objectObjectRedisTemplate = new RedisTemplate<>();
        objectObjectRedisTemplate.setConnectionFactory(factory);
        //配置序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //jackjson
        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectJackson2JsonRedisSerializer.setObjectMapper(objectMapper);


//        objectObjectRedisTemplate.setStringSerializer(stringRedisSerializer);
        objectObjectRedisTemplate.setDefaultSerializer(stringRedisSerializer);
        objectObjectRedisTemplate.setValueSerializer(objectJackson2JsonRedisSerializer);
        objectObjectRedisTemplate.afterPropertiesSet();
        template = objectObjectRedisTemplate;
        return objectObjectRedisTemplate;
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {

        return tryLock(10, TimeUnit.SECONDS);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        String uuid = UUID.randomUUID().toString().toLowerCase().replace("-", "");
        Boolean aBoolean = template.opsForValue().setIfAbsent(lockKey, uuid, time, unit);
//        Boolean aBoolean = template.opsForValue().setIfAbsent(lockKey, lockKey);
        if (aBoolean) {
            System.out.println("加锁\t" + uuid);
            local.set(uuid);
            return true;
        }
        return false;

    }

    @Override
    public void unlock() {
        Object args = JSONObject.toJSON(local.get());
//        System.out.println("JSONObject 后" + args);
        System.out.println("解锁\t" + args);
        template.execute(script, Collections.singletonList(lockKey), args);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

