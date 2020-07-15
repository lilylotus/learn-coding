package cn.nihility.boot.mybatis;

import cn.nihility.boot.mybatis.dto.Bank;
import cn.nihility.boot.mybatis.dto.BankLogging;
import cn.nihility.boot.mybatis.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * BankTest
 * spring 事务管理操作， 银行转账案例测试
 * @author clover
 * @date 2020-01-16 12:31
 */
@SpringBootTest
public class BankTest {

    @Autowired
    private CreateBankAccountService createBankAccountService;
    @Autowired
    private QueryAccountInfoService queryAccountInfoService;
    @Autowired
    private QueryTransferLogService queryTransferLogService;
    /*@Autowired
    private TransferLogService transferLogService;*/
    @Autowired
    private TransferService transferService;

    @Test
    public void testQueryTransferAndLogInfo() {

        String operator = "爱爱1";

        Bank bankInfo = queryAccountInfoService.queryDetailInfo(operator);
        List<BankLogging> bankLoggings = queryTransferLogService.query(operator);

        Assertions.assertNotNull(bankInfo);
        Assertions.assertNotNull(bankLoggings);

        System.out.println(bankInfo);
        System.out.println(bankLoggings);
    }

    @Test
    public void testCreateBankAccount() {
        Bank account = new Bank();
        account.setName("小康");
        account.setGender("女");
        account.setAddress("不知所踪");
        account.setMoney(100000);

        boolean result = createBankAccountService.createNewBankAccount(account);
        Assertions.assertEquals(result, true);
    }

    @Test
    public void testBankTransferAndLog() throws Exception {
        String operator = "小康"; String to = "爱爱1"; int money = 200;
        boolean transfer = transferService.transfer(to, operator, money);
//        boolean log = transferLogService.recordTransferInfo(operator, to, money);

        Assertions.assertEquals(transfer, true);
//        Assert.assertEquals(log, true);
    }

    /* ============================================================ */
    String operator = "爱爱1"; String to = "小康"; int money = 100;

    @Test
    public void testTransactionRequired() throws Exception {
        boolean transfer = transferService.transfer(operator, to, money);
        Assertions.assertEquals(transfer, true);
    }

}
