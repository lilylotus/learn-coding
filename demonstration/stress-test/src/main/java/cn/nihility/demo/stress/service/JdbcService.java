package cn.nihility.demo.stress.service;

import cn.nihility.common.util.SnowflakeIdWorker;
import cn.nihility.common.util.UuidUtils;
import cn.nihility.demo.stress.mapper.ServiceLogMapper;
import cn.nihility.demo.stress.pojo.ServiceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JdbcService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcService.class);

    private ServiceLogMapper serviceLogMapper;

    public JdbcService(ServiceLogMapper serviceLogMapper) {
        this.serviceLogMapper = serviceLogMapper;
    }

    public void addServiceLog() {
        Integer result = serviceLogMapper.insertByEntity(buildServiceLog("测试内容 [" + UuidUtils.jdkUUID() + "]"));
        logger.info("随机添加一条操作日志，结果 [{}]", result);
    }

    public void recordServiceLog(String content) {
        Integer result = serviceLogMapper.insertByEntity(buildServiceLog(content));
        logger.info("记录一条操作日志 [{}] 结果 [{}]", content, result);
    }

    private ServiceLog buildServiceLog(String content) {
        ServiceLog log = new ServiceLog();
        String user = UuidUtils.jdkUUID(10);
        log.setId(SnowflakeIdWorker.nextSnowId());
        log.setOperation("测试业务");
        log.setContent(content);
        log.setAddBy(user);
        log.setAddTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        return log;
    }

}
