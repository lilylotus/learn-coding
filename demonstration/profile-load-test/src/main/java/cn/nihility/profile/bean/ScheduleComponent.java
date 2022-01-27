package cn.nihility.profile.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

//@Component
public class ScheduleComponent {

    private static final Logger log = LoggerFactory.getLogger(ScheduleComponent.class);

    @Scheduled(cron = "0/10 * * * * ?")
    @Async("scheduleLogTaskThreadPool")
    public void scheduleTask() {
        log.trace("定时任务 Trace 日志");
        log.debug("定时任务 debug 日志");
        log.info("定时任务 info 日志");
        log.warn("定时任务 warn 日志");
        log.error("定时任务 error 日志");
    }

}
