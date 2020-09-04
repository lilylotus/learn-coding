package cn.nihility.boot.mybatis.service.impl;

import cn.nihility.boot.mybatis.dao.BankDao;
import cn.nihility.boot.mybatis.service.TransferLogService;
import cn.nihility.boot.mybatis.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * TransferServiceImpl
 * 具体的 bank 转账实现
 * @author clover
 * @date 2020-01-16 12:06
 */
@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final BankDao bankDao;
    private final TransferLogService transferLogService;

    public TransferServiceImpl(BankDao bankDao, TransferLogService transferLogService) {
        this.bankDao = bankDao;
        this.transferLogService = transferLogService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean transfer(String operatorName, String toName, int money) throws Exception {
        logger.info("Transfer start, [{}] -> [{}] -> [{}]", operatorName, toName, money);

        int info = bankDao.transferOutMoneyByName(operatorName, money);
        int result = bankDao.transferInMoneyByName(toName, money);

//        boolean b = transferLogService.recordTransferInfo(operatorName, toName, money);
        try {
            boolean b = transferLogService.recordTransferInfo(operatorName, toName, money);
            logger.info("transfer log record result [{}]", b);
        } catch (Exception e) {
            System.out.println("处理内部异常");
            e.printStackTrace();
        }

        logger.info("Transfer over, operation info, transfer out [{}], transfer in [{}]", info, result);

//        System.out.println("outer error " + (1 / 0));

        return info > 0 && result > 0;
    }

}
