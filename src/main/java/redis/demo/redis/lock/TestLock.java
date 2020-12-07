package redis.demo.redis.lock;

import org.springframework.data.redis.core.RedisTemplate;

import javax.lang.model.element.VariableElement;
import java.util.*;
import java.util.concurrent.*;

/**
 * @ClassName: TestLock
 * @Description: 模拟10个线程对全局唯一的数num进行+1 操作 ,通过redis锁来保证并发关系，保证最后结果为100 表示成功
 * @Author fjp
 * @Date 2020/12/4-19:55
 * @Version 1.0
 */

public class TestLock {

    static Integer num = 0; //并发访问的数字
    static int time = 100;
    static CountDownLatch countDownLatch = new CountDownLatch(time);

//    static RedisLock redisLock = new RedisLock("lockKey");
//   RedisLock redisLock = new RedisLock("lockKey");


    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        //初始化脚本和Template
        RedisLock redisLock = new RedisLock("lockKey");
        RedisTemplate redisTemplate = redisLock.initTemplate();
        redisLock.getScriptByTemplate("unlock.lua");
        System.out.println("开始");
        int i = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (; i < time; i++) {

            Task task = new Task(redisLock);
            executorService.execute(task);
            countDownLatch.countDown();
        }

        Thread.sleep(10000);//等待下5s
        System.out.println(Thread.currentThread().getName() + "主线程等待子线程完成");
        System.out.println("最终并发访问的num数字是：" + num);
        executorService.shutdown();
    }

    public static class Task implements Runnable {
        private RedisLock redisLock;

        Task(RedisLock redisLock) {
            this.redisLock = redisLock;

        }

        @Override
        public void run() {
            try {
                countDownLatch.await();// 100个并发蓄势待发攻击
                // redis 分布锁式大招，一夫当关！
                redisLock.lock();
                Thread.sleep(50);
                num = num + 1;
                System.out.println(Thread.currentThread().getName()+"计算结果为："+num);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //死都要解锁，不留死锁后患！
                redisLock.unlock();
            }
        }
    }


}

