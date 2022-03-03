package cn.nihility.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * {
 * "access_token":"2YotnFZFEjr1zCsicMWpAA",
 * "token_type":"example",
 * "expires_in":3600,
 * "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
 * "example_parameter":"example_value"
 * }
 *
 * @author nihility
 * @date 2022/02/21 11:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Oauth2Response {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @Override
    public String toString() {
        return "Oauth2Response{" +
            "accessToken='" + accessToken + '\'' +
            ", refreshToken='" + refreshToken + '\'' +
            ", tokenType='" + tokenType + '\'' +
            ", expiresIn=" + expiresIn +
            '}';
    }

}
