package cn.nihility.boot.mybatis.service;

/**
 * TransferService
 * bank 银行转账的接口
 * @author clover
 * @date 2020-01-16 11:51
 */
public interface TransferService {

    /**
     * 金额转账
     * @param operatorName 操作者的名称
     * @param toName 要转给用户的名称
     * @param money 转账金额
     * @return 转账是否成功
     */
    boolean transfer(String operatorName, String toName, int money) throws Exception;

}
