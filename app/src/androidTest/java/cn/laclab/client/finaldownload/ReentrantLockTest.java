package cn.laclab.client.finaldownload;

import android.test.AndroidTestCase;
import android.util.Log;

import cn.laclab.client.finaldownload.core.study.ReentrantLockDemo;

/**
 * Created by sinye on 15/10/27.
 */
public class ReentrantLockTest extends AndroidTestCase{
    public static final String TAG = "ReentrantLockTest";
    public void testLock() {
        final ReentrantLockDemo.BoundedBuffer boundedBuffer = new ReentrantLockDemo.BoundedBuffer();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        boundedBuffer.put(Integer.valueOf(i));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Object val = boundedBuffer.take();
                        System.out.println(val);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        t1.start();
//        t2.start();
    }
}
