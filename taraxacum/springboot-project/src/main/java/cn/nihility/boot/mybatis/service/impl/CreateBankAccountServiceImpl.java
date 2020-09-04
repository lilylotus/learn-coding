package cn.nihility.boot.mybatis.service.impl;

import cn.nihility.boot.mybatis.dao.BankDao;
import cn.nihility.boot.mybatis.dto.Bank;
import cn.nihility.boot.mybatis.service.CreateBankAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CreateBankAccountServiceImpl
 *
 * @author clover
 * @date 2020-01-16 12:24
 */
@Service
public class CreateBankAccountServiceImpl implements CreateBankAccountService {

    private static final Logger logger = LoggerFactory.getLogger(CreateBankAccountServiceImpl.class);

    private final BankDao bankDao;

    public CreateBankAccountServiceImpl(BankDao bankDao) {
        this.bankDao = bankDao;
    }

    @Override
    public boolean createNewBankAccount(Bank account) {

        // 这里应该有一系列的账户信息校验

        int result = bankDao.insertNewBankAccount(account);
        logger.info("create new bank account result [{}]", result);

        return result > 0;
    }
}
