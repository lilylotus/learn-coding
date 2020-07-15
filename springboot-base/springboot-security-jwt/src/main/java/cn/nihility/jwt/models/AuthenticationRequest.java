package cn.nihility.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthenticationRequest
 *
 * @author dandelion
 * @date 2020-03-24 20:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    private String name;
    private String password;

}
