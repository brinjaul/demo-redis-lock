package redis.demo.redis;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TestRedis
 * @Description: 测试脚本
 * @Author fjp
 * @Date 2020/12/2-21:11
 * @Version 1.0
 *
 *
 * <dependency>
 * <groupId>org.springframework.boot</groupId>
 * <artifactId>spring-boot-starter-data-redis</artifactId>
 * <version>2.1.7.RELEASE</version>
 *
 * </dependency>
 * <dependency>
 * <groupId>redis.clients</groupId>
 * <artifactId>jedis</artifactId>
 * <version>2.9.0</version>
 * </dependency>
 * <dependency>
 * <groupId>com.fasterxml.jackson.core</groupId>
 * <artifactId>jackson-databind</artifactId>
 * <version>2.10.3</version>
 * <scope>compile</scope>
 * </dependency>
 */
public class TestRediSpringTemplate {

    public static void main(String[] args) {

        final TestRediSpringTemplate currentTest = new TestRediSpringTemplate();
        RedisTemplate redisTemplate = currentTest.initTemplate();
        DefaultRedisScript script = currentTest.getScriptByTemplate("unlock.lua");


        redisTemplate.opsForValue().set("testkey1", "testvalue1"); //普通的set 方法
        redisTemplate.opsForValue().set("testkey2", "testvalue2"); //普通的set 方法
        redisTemplate.opsForValue().set("testkey3", "testvalue3"); //普通的set 方法


        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("mapK","mapV");
        Object o = JSONObject.toJSON(objectObjectHashMap);//作为json  串传给args 但如果 lua 不用cjson解析 则是整个对象的一个数组
        //redis  提供新命令  保证原子性  set nx  和 ex  同时设置
        redisTemplate.opsForValue().setIfAbsent("lockKey", "currentPart", 100L, TimeUnit.SECONDS);


        List<String> testkey2 = Collections.singletonList("testkey2");
        List<String> testkey3 = Collections.singletonList("testkey3");//作为args 也可以 但是变成了对象
        //使用lua  脚本判断value ，判断成功并删除
        Object execute2 = redisTemplate.execute(script, testkey2, "testkey2");
        Object execute3 = redisTemplate.execute(script, testkey3, "testkey3");

       //打印结果
        System.out.println(execute2.toString());
        System.out.println(execute3.toString());


    }




    public RedisTemplate initTemplate() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
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

        return objectObjectRedisTemplate;
    }

    public DefaultRedisScript getScriptByTemplate(String path) {
        DefaultRedisScript defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(String.class);
//        defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));
        defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        return defaultRedisScript;
    }


    public String getScript(String path) {
        StringBuilder script = new StringBuilder();
        final URL resource = TestRediSpringTemplate.class.getClassLoader().getResource(path);
        try (

                InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(path);//返回的是BufferInpustSream
                InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                script.append(str);
            }
            System.out.println(script.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return script.toString();
    }
}

