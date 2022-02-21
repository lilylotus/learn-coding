package cn.nihility.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/21 10:23
 */
@Setter
@Getter
public class UserInfo {

    private String userId;

    private String userName;

    private Map<String, Object> ext;

}
