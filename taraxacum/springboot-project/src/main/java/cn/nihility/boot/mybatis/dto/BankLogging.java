package cn.nihility.boot.mybatis.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * BankLogging
 *
 * @author clover
 * @date 2020-01-16 11:27
 */
@Data
public class BankLogging {

    private Integer id;
    private String operator;
    private String transferTo;
    private LocalDateTime operationTime;
    private Integer money;

}