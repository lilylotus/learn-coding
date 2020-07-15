package cn.nihility.io.bio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BioServerHeadlerExecutorPool
 *
 * @author dandelion
 * @date 2020-04-19 16:19
 */
public class BioServerHeadlerExecutorPool {

    private ExecutorService executor;

    public BioServerHeadlerExecutorPool(int maxPoolSize, int queueSize) {
        this.executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                maxPoolSize, 10L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }
}
