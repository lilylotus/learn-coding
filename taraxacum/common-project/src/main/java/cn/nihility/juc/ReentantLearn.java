package cn.nihility.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentantLearn
 *
 * @author dandelion
 * @date 2020-04-13 15:23
 */
public class ReentantLearn {

    public static void main(String[] args) {

        ReentrantLock reentrantLock = new ReentrantLock();
        final Condition condition = reentrantLock.newCondition();

        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        condition.signal();

    }

}
