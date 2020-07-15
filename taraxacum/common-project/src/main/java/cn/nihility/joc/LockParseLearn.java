package cn.nihility.joc;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * LockParseLearn
 *
 * @author dandelion
 * @date 2020-05-11 19:16
 */
public class LockParseLearn {

    public static void main(String[] args) throws InterruptedException {
        PrintWriter out = new PrintWriter(System.out, true);
        out.println(VM.current().details());

        TimeUnit.SECONDS.sleep(5);

        final A a = new A();
        final ClassLayout layout = ClassLayout.parseInstance(a);

        out.println("****** Fresh Object.");
        out.println(layout.toPrintable());

        CountDownLatch latch = new CountDownLatch(2);
        final ExecutorService services = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 2; i++) {
            services.execute(() -> {
                out.println("****** " + Thread.currentThread().getName() + " execute the lock");
                out.println(layout.toPrintable());
                synchronized (a) {
                    latch.countDown();
                    out.println("****** " + Thread.currentThread().getName() + " With the lock");
                    out.println(layout.toPrintable());
                }
            });
        }
        latch.await();
        services.shutdown();

        out.println("****** After the lock");
        out.println(layout.toPrintable());

    }

    static class A {}

}
