package cn.nihility.juc;

/**
 * ThreadWaitLearn
 *
 * @author dandelion
 * @date 2020-04-21 11:10
 */
public class ThreadWaitLearn {

    private static final Object lockObject = new Object();

    public static void main(String[] args) {

        /*Thread t = new Thread(() -> System.out.println("Hello"));

        t.interrupt();
        final boolean interrupted = t.isInterrupted();

        // 会清除当前线程的中断状态
        final boolean interrupted1 = Thread.interrupted();*/

        ThreadWaitLearn threadWaitLearn = new ThreadWaitLearn();
        threadWaitLearn.waitAndNotify();

    }


    public void waitAndNotify() {


        Thread t1 = new Thread(() -> {
            synchronized (lockObject) {
                System.out.println("T1 start " + System.currentTimeMillis());

                try {
                    System.out.println("T1 wait Object");
                    lockObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("T1 end " + System.currentTimeMillis());
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockObject) {
                System.out.println("T2 start Notify thread t1 " + System.currentTimeMillis());

                lockObject.notify();

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("T2 end " + System.currentTimeMillis());
            }
        });

        t1.start();
        t2.start();

    }

}
