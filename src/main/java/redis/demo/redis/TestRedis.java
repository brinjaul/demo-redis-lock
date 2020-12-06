package redis.demo.redis;//package com.mytest.redis;
//
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPoolConfig;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//
///**
// * @ClassName: TestRedis
// * @Description:   用原生的jedis 来测试，需要将依赖写到pom中，注释现有的redis 相关的pom
// * @Author fjp
// * @Date 2020/12/2-21:11
// * @Version 1.0
// *
// * <dependency>
// *   <groupId>org.springframework.data</groupId>
// *   <artifactId>spring-data-redis</artifactId>
// *   <version>1.7.1.RELEASE</version>
// *   </dependency>
// *
// *
// *
// * <dependency>
// * <groupId>redis.clients</groupId>
// * <artifactId>jedis</artifactId>
// * <version>2.8.0</version>
// * <type>jar</type>
// * <scope>compile</scope>
// * </dependency>
// */
//public class TestRedis {
//
//    public static void main(String[] args) {
//
//
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(10);
//        jedisPoolConfig.setMaxTotal(10000);
//
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//        jedisConnectionFactory.setHostName("127.0.0.1");
//        jedisConnectionFactory.setPort(6379);
////        jedisConnectionFactory.setPassword("12345678");
//        jedisConnectionFactory.setUsePool(true);
//        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
//        jedisConnectionFactory.afterPropertiesSet();//构造sharInfo 传参
//
//        Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection();
//
//        String set = jedis.set("testkey", "testvalue");
//
//         TestRedis testRedis = new TestRedis();
//         String script = testRedis.getScript("getSet.lua");
//
//
//        final ArrayList<String> listKey = new ArrayList<>();
//        listKey.add("fjp");
//
//        final ArrayList<String> listValue = new ArrayList<>();
//        listValue.add("fjp");
//        jedis.eval(script,listKey,listValue);
//
//    }
//
//    public  String getScript(String path) {
//        StringBuilder script = new StringBuilder();
//        final URL resource = TestRedis.class.getClassLoader().getResource(path);
//        try(
//
//        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(path);//返回的是BufferInpustSream
//        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
//        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
//        ) {
//            String str = "";
//            while ((str = bufferedReader.readLine())!=null){
//                script.append(str);
//            }
//            System.out.println(script.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return script.toString();
//    }
//}
//
