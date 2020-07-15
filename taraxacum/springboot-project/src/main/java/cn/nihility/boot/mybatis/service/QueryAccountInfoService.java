package cn.nihility.boot.mybatis.service;


import cn.nihility.boot.mybatis.dto.Bank;

/**
 * QueryAccountInfoService
 * 查询账户信息接口
 * @author clover
 * @date 2020-01-16 11:55
 */
public interface QueryAccountInfoService {

    /**
     * 查询当前用户账户信息
     * @param operator 操作用户
     * @return
     */
    Bank queryDetailInfo(String operator);

}
