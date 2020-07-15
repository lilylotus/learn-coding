package cn.nihility.boot.mybatis.dao;

import cn.nihility.boot.mybatis.dto.Bank;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * BankDao
 *
 * @author clover
 * @date 2020-01-16 11:35
 */
@Mapper
public interface BankDao {

    List<Bank> queryAll();
    Bank queryByName(String name);
    int transferOutMoneyByName(String name, int money);
    int transferInMoneyByName(String name, int money);

    int insertNewBankAccount(Bank account);

}
