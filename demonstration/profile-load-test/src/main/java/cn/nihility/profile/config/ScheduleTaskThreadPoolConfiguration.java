package cn.nihility.profile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 1. 没有配置自定义线程池时，会默认使用 {@link SimpleAsyncTaskExecutor}。
 * 2. 只配置了一个线程池，不需要显示指定使用这个线程池，
 * spring 会自动使用该唯一的线程池，但如果配置了多个就必须要显示指定，否则还是会使用默认的。
 * 3. 如果想要指定使用哪个线程池，使用 @Async("executor2") 显示指定。
 */
@Configuration
// 开启定时任务
@EnableScheduling
// 开启异步任务执行
//@EnableAsync
public class ScheduleTaskThreadPoolConfiguration {

    @Bean
    public ThreadPoolTaskExecutor scheduleLogTaskThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(32);
        executor.setThreadNamePrefix("ScheduleLogTask-");
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor commonScheduleTaskThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(128);
        executor.setThreadNamePrefix("CommonScheduleTask-");
        return executor;
    }

    /**
     * {@link Scheduled} 默认是单线程
     * 无论有多个 @Scheduled 注解的方法，都是单线程调度执行的，有多个时，需要配置线程池
     */
    @Configuration
    static class ScheduleThreadPoolConfiguration implements SchedulingConfigurer {

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

            executor.setCorePoolSize(8);
            executor.setMaxPoolSize(8);
            executor.setQueueCapacity(64);
            executor.setThreadNamePrefix("ScheduleThreadPool-");

            scheduler.setThreadFactory(executor);
            scheduler.initialize();

            taskRegistrar.setTaskScheduler(scheduler);
        }

    }

}
