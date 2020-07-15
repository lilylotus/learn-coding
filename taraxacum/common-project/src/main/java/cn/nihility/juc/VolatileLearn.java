package cn.nihility.juc;

/**
 * VolatileLearn
 *
 * @author dandelion
 * @date 2020-05-09 10:18
 */
public class VolatileLearn {

    private static volatile boolean found = false;

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            System.out.println("waiting found resource ...");

            while (!found) {}

            System.out.println("resource found, begin ...");
        }, "Thread-1");

        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ready for resource success.");
            found = true;
        }, "thread-2");

        t2.start();
    }



}
