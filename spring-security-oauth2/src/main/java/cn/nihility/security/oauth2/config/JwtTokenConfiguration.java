package cn.nihility.security.oauth2.config;

import cn.nihility.security.oauth2.service.MyOAuth2AccessTokenEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author orchid
 * @date 2021-05-13 00:22:30
 */
@Configuration
public class JwtTokenConfiguration {

    @Bean
    public TokenStore jwtAccessTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("jwtAccessTokenKey");
        return converter;
    }

    @Bean
    public TokenEnhancer myOAuth2AccessTokenEnhancer() {
        return new MyOAuth2AccessTokenEnhancer();
    }

}
