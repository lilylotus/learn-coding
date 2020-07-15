package cn.nihility.boot.mybatis.dto;

import lombok.Data;

/**
 * Bank
 *
 * @author clover
 * @date 2020-01-16 11:26
 */
@Data
public class Bank {

    private Integer id;
    private String name;
    private String gender;
    private String address;
    private Integer money;

}