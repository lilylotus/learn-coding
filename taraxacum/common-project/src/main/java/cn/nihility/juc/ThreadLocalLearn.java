package cn.nihility.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadLocalLearn
 *
 * @author dandelion
 * @date 2020-04-27 22:46
 */
public class ThreadLocalLearn {

    public static void main(String[] args) {

        ThreadLocal<User> threadLocal = new ThreadLocal<>();


        Thread local = new Thread(() -> {
            User user = new User("你好");
            threadLocal.set(user);

            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final User user1 = threadLocal.get();
                System.out.println(user1);
            }
        });

        Thread local1 = new Thread(() -> {
            List<byte[]> list = new ArrayList<>();
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] buffer = new byte[1024 * 1024];
                list.add(buffer);
            }
        });

        local.start();
        local1.start();

    }

    public static void main1(String[] args) {
        ThreadLocal<User> threadLocal = new ThreadLocal<>();
        final ExecutorService service = Executors.newFixedThreadPool(10);
        final User user = new User("你好");
        final Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                threadLocal.set(user);
                for (;;) {
                    System.out.println("current thread " + Thread.currentThread().getName());
                    final User u = threadLocal.get();
                    System.out.println(Thread.currentThread().getName() + " : " + u.name);
                    try {
                        Thread.sleep(random.nextInt(30) * 100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String currentName = Thread.currentThread().getName() + ":" + random.nextInt(100);
                    System.out.println("Current Name : " + currentName);
                    u.name = currentName;
                }
            });
        }

        service.shutdown();
    }


    static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
