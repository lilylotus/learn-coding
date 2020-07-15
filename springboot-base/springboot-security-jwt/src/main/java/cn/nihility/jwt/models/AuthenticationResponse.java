package cn.nihility.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AuthenticationResponse
 *
 * @author dandelion
 * @date 2020-03-24 20:28
 */
@Data
@AllArgsConstructor
public class AuthenticationResponse {

    private final String jwt;

}
