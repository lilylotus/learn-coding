package cn.nihility.boot.mybatis.service;

/**
 * TransferLogService
 * 记录账户转账记录的流水信息
 * @author clover
 * @date 2020-01-16 11:57
 */
public interface TransferLogService {

    boolean recordTransferInfo(String operator, String toName, int money) throws Exception;

}
