package cn.nihility.boot.mybatis.service;

import cn.nihility.boot.mybatis.dto.Bank;

/**
 * CreateBankAccountService
 * 创建一个账户的接口
 * @author clover
 * @date 2020-01-16 12:20
 */
public interface CreateBankAccountService {

    /**
     * 创建一个银行账户
     * @param account 要创建银行账户的信息
     * @return 是否创建成功
     */
    boolean createNewBankAccount(Bank account);

}
