package redis.demo.redis.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: TestLock
 * @Description: 模拟100个线程对全局唯一的数num进行+1 操作 ,通过redis锁来保证并发关系，保证最后结果为100 表示成功
 * @Author fjp
 * @Date 2020/12/4-19:55
 * @Version 1.0
 */

public class TestLock {

    static Integer num = 0;
    static HashMap map = new HashMap();
    static int time = 100;
    static CountDownLatch countDownLatch = new CountDownLatch(time);
    static RedisLock redisLock = new RedisLock("lockKey");

    public static void main(String[] args) throws InterruptedException {
        //初始化脚本和Template
        redisLock.initTemplate();
        redisLock.getScriptByTemplate("unlock.lua");
        System.out.println("开始");
        int i = 0;
        for (; i < time; i++) {
            Task task = new Task();
            new Thread(task).start();

            countDownLatch.countDown();
        }
        Thread.sleep(3000);//等待下
        int size2 = map.size();
        System.out.println(Thread.currentThread().getName() + "主线程等待子线程完成");

        System.out.println("map总size" + size2);
        System.out.println("并发访问的num数字是：" + num);

    }

    public static class Task implements Runnable {
        Task() {
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();// 100个并发蓄势待发攻击
                // redis 分布锁式大招，一夫当关！
                redisLock.lock();
                Thread.sleep(200);
                map.put(num, num);
                num = num + 1;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //死都要解锁，不留死锁后患！
                redisLock.unlock();
            }
        }
    }


}

