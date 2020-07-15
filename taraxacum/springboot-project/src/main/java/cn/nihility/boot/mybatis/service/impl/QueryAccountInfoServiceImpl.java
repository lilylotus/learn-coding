package cn.nihility.boot.mybatis.service.impl;

import cn.nihility.boot.mybatis.dao.BankDao;
import cn.nihility.boot.mybatis.dto.Bank;
import cn.nihility.boot.mybatis.service.QueryAccountInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * QueryAccountInfoServiceImpl
 *
 * @author clover
 * @date 2020-01-16 12:29
 */
@Service
public class QueryAccountInfoServiceImpl implements QueryAccountInfoService {

    private static final Logger logger = LoggerFactory.getLogger(QueryAccountInfoServiceImpl.class);

    @Autowired
    private BankDao bankDao;

    @Override
    public Bank queryDetailInfo(String operator) {
        logger.info("查询银行账户信息, 操作员 [{}]", operator);
        return bankDao.queryByName(operator);
    }

}
