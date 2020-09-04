package cn.nihility.boot.mybatis.service.impl;

import cn.nihility.boot.mybatis.dao.BankLoggingDao;
import cn.nihility.boot.mybatis.dto.BankLogging;
import cn.nihility.boot.mybatis.service.QueryTransferLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * QueryTransferLogServiceImpl
 *
 * @author clover
 * @date 2020-01-16 12:27
 */
@Service
public class QueryTransferLogServiceImpl implements QueryTransferLogService {

    private static final Logger logger = LoggerFactory.getLogger(QueryTransferLogServiceImpl.class);

    private final BankLoggingDao bankLoggingDao;

    public QueryTransferLogServiceImpl(BankLoggingDao bankLoggingDao) {
        this.bankLoggingDao = bankLoggingDao;
    }

    @Override
    public List<BankLogging> query(String operator) {
        logger.info("query transfer info, operator [{}]", operator);
        return bankLoggingDao.queryDetailsByOperator(operator);
    }
}
