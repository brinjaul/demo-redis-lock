package redis.demo.redis.lock;

import java.util.concurrent.locks.Lock;

/**
 * @ClassName: AbstractLock
 * @Description:
 * @Author fjp
 * @Date 2020/11/30-13:43
 * @Version 1.0
 */
public abstract class AbstractLock implements Lock {

     ThreadLocal<String> local = new ThreadLocal<>();



    /*

    * 0 调用lock   内部实现是 trylock
    * 1 尝试获取lock
    * 2 获取成功返true
    * 3 失败则睡眠100ms
    *    递归
    *
    * */

    @Override
    public void lock() {
        if(tryLock()){
            return ;
        }else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lock();
    }


}

