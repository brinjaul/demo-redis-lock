package redis.demo.redis;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * @ClassName: TestRedisPool
 * @Description:
 * @Author fjp
 * @Date 2020/12/6-23:10
 * @Version 1.0
 */
public class TestRedisPool {
    public static void main(String[] args) {
        new JedisConnectionFactory();
    }
}

