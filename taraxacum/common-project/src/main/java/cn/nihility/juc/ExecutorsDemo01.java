package cn.nihility.juc;

import org.apache.commons.lang3.time.StopWatch;

import java.util.Random;
import java.util.concurrent.*;

/**
 * ExecutorsDemo01
 *
 * @author clover
 * @date 2020-03-02 22:24
 */
public class ExecutorsDemo01 {


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final ExecutorService service = Executors.newSingleThreadExecutor();
        /*final Future<?> submit = service.submit(() -> {
            System.out.println(1 / 0);
        });*/

        service.execute(() -> {
            System.out.println(1/0);
        });

        /*final Object o = submit.get();
        System.out.println(o);*/

        service.shutdown();


//        newFixedThreadPool();

//        scheduleAtFixedRate();
//        scheduleWithFixedDelay();
    }

    /**
     * FixedDelay 当线程执行完成后 等待 指定时间在执行下一个任务
     */
    public static void scheduleWithFixedDelay() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

        final Random random = new Random(System.currentTimeMillis());
        final StopWatch sw = new StopWatch();
        sw.start();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            int rand = random.nextInt(4);
            try {
                Thread.sleep(rand * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("id = " + Thread.currentThread().getId() + " rand = " + rand + " : time = " + sw.getTime(TimeUnit.SECONDS));
        }, 1, 2, TimeUnit.SECONDS);

    }

    /**
     * FixedRate 这个是在线程运行完后，若完成时间小于 rate ，则会等待到达 rate 在执行下一个任务。
     * 若是线程执行时间大于 rate 这该线程完成后立马执行下一个线程
     */
    public static void scheduleAtFixedRate() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        final Random random = new Random(System.currentTimeMillis());
        final StopWatch sw = new StopWatch();
        sw.start();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int rand = random.nextInt(4);
            try {
                Thread.sleep(rand * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("id = " + Thread.currentThread().getId() + " rand = " + rand + " : time = " + sw.getTime(TimeUnit.SECONDS));
        }, 1, 2, TimeUnit.SECONDS);

    }

    public static void newFixedThreadPool() {

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 20; i++) {
            final int index = i;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executorService.execute(() ->
                    System.out.println("id = " + Thread.currentThread().getId() + " count = " + index));
        }

        executorService.shutdown();
    }

    public static void newCachedThreadPool() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 100; i++) {
            final int index = i;
            executorService.execute(() -> {
                System.out.println("id = " + Thread.currentThread().getId() + " count = " + index);
            });
        }

        executorService.shutdown();
    }

}
