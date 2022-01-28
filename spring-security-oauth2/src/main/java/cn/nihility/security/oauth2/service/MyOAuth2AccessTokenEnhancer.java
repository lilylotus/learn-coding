package cn.nihility.security.oauth2.service;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author orchid
 * @date 2021-05-13 00:33:40
 */
public class MyOAuth2AccessTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> additionalInformation = new HashMap<>(8);
        additionalInformation.put("enhancer", "MyOAuth2AccessTokenEnhancer info");
        additionalInformation.put("authenticationName", authentication.getName());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

        return accessToken;
    }

}
