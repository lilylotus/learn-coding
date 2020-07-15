package cn.nihility.boot.mybatis.service;

import cn.nihility.boot.mybatis.dto.BankLogging;

import java.util.List;

/**
 * QueryTransferLogService
 * 查询操作用户交易流水信息
 * @author clover
 * @date 2020-01-16 12:04
 */
public interface QueryTransferLogService {

    /**
     * 查询当前操纵账户的操作流水记录
     * @param operator 操作用户
     * @return 最近的操作记录信息
     */
    List<BankLogging> query(String operator);

}
