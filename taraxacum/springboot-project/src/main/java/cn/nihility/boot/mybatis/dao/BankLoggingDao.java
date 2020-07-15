package cn.nihility.boot.mybatis.dao;

import cn.nihility.boot.mybatis.dto.BankLogging;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * BankLoggingDao
 *
 * @author clover
 * @date 2020-01-16 11:38
 */
@Mapper
public interface BankLoggingDao {

    List<BankLogging> queryAll();
    List<BankLogging> queryDetailsByOperator(String name);

    int insertTransferLog(BankLogging log);

}
