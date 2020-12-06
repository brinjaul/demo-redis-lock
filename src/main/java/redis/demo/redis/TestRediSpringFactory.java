package redis.demo.redis;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * @ClassName: TestRedis
 * @Description:    原生的方法来测试脚本
 * @Author fjp
 * @Date 2020/12/2-21:11
 * @Version 1.0
 *使用的依赖
 * <dependency>
 *   <groupId>org.springframework.data</groupId>
 *   <artifactId>spring-data-redis</artifactId>
 *   <version>1.7.1.RELEASE</version>
 *   </dependency>
 *
 *
 *
 * <dependency>
 * <groupId>redis.clients</groupId>
 * <artifactId>jedis</artifactId>
 * <version>2.8.0</version>
 * <type>jar</type>
 * <scope>compile</scope>
 * </dependency>
 */
public class TestRediSpringFactory {

    public static void main(String[] args) {
        //连接单节点的配置
         RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
         config.setDatabase(0);
         config.setPort(6379);
         config.setHostName("127.0.0.1");
        //连接工厂，关联配置
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        //获取连接
         RedisConnection connection = factory.getConnection();
         //set 一个测试值
         connection.set("testkey2".getBytes(),"testvalue2".getBytes());

         TestRediSpringFactory testRedis = new TestRediSpringFactory();
        //测试脚本的api  获取脚本字符串  注意里面不能有注释
         String script = testRedis.getScript("unlock.lua");

        Object eval = connection.eval(script.getBytes(), ReturnType.INTEGER, 1, "testkey2".getBytes(), "testvalue2".getBytes());//故意保证后面两个参数相等，这样unlock.lua会判断ture,进行删除
        System.out.println(eval);

    }






    public  String getScript(String path) {
        StringBuilder script = new StringBuilder();
        final URL resource = TestRediSpringFactory.class.getClassLoader().getResource(path);
        try(

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(path);//返回的是BufferInpustSream
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String str = "";
            while ((str = bufferedReader.readLine())!=null){
                script.append(str+"\n");//没有换行lua 解析不了
            }
            System.out.println(script.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return script.toString();
    }
}

