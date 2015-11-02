package cn.laclab.client.finaldownload.core.study;

import android.util.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sinye on 15/10/27.
 */
public class ReentrantLockDemo {
    /**
     * BoundedBuffer 是一个定长100的集合，当集合中没有元素时，take方法需要等待，直到有元素时才返回元素
     * 当其中的元素数达到最大值时，要等待直到元素被take之后才执行put的操作
     */
    public static class BoundedBuffer {
        final Lock lock = new ReentrantLock();
        final Condition notFull = lock.newCondition();
        final Condition notEmpty = lock.newCondition();

        final Object[] items = new Object[10];
        int putptr, takeptr, count;

        public void put(Object x) throws InterruptedException {
            lock.lock();
            Log.d("TAG", "put get lock");
            try {
                while (count == items.length) {
                    Log.d("TAG", "buffer full, please wait");
                    notFull.await();
                }
                items[putptr] = x;
                Log.d("TAG", "put num="+putptr);
                if (++putptr == items.length)
                    putptr = 0;
                ++count;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }
        public Object take() throws InterruptedException {
            Log.d("TAG", "take wait lock");
            lock.lock();
            Log.d("TAG", "take get lock");
            try {
                while (count == 0) {
                    Log.d("TAG", "no elements, please wait");
                    notEmpty.await();
                }
                Object x = items[takeptr];
                Log.d("TAG", "take num="+takeptr);
                if (++takeptr == items.length)
                    takeptr = 0;
                --count;
                notFull.signal();
                return x;
            } finally {
                lock.unlock();
            }
        }
    }


}
