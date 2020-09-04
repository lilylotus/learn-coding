package cn.nihility.boot.mybatis.service.impl;

import cn.nihility.boot.mybatis.dao.BankLoggingDao;
import cn.nihility.boot.mybatis.dto.BankLogging;
import cn.nihility.boot.mybatis.service.TransferLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * TransferLogServiceImpl
 * 记录账户转账流水的日志
 * @author clover
 * @date 2020-01-16 12:12
 */
@Service
public class TransferLogServiceImpl implements TransferLogService {

    private static final Logger logger = LoggerFactory.getLogger(TransferLogServiceImpl.class);

    private final BankLoggingDao bankLoggingDao;

    public TransferLogServiceImpl(BankLoggingDao bankLoggingDao) {
        this.bankLoggingDao = bankLoggingDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean recordTransferInfo(String operator, String toName, int money) throws IOException {
        logger.info("Transfer recording start, [{}] --> [{}] --> [{}]", operator, toName, money);

        BankLogging log = new BankLogging();
        log.setMoney(money);
        log.setOperator(operator);
        log.setTransferTo(toName);

        int result = bankLoggingDao.insertTransferLog(log);

//        System.out.println("print error " + (1 / 0));
//        if (true) { throw new IOException("transfer error"); }

        logger.info("Transfer recording over, log result [{}]", result);
        return result > 0;
    }
}
