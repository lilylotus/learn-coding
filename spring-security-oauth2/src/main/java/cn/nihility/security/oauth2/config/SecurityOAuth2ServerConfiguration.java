package cn.nihility.security.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author orchid
 * @date 2021-05-11 01:01:34
 */
@Configuration
@EnableAuthorizationServer
public class SecurityOAuth2ServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsServiceImpl;
    private AuthenticationManager authenticationManagerBean;
    private TokenStore jwtAccessTokenStore;
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private TokenEnhancer myOAuth2AccessTokenEnhancer;

    public SecurityOAuth2ServerConfiguration(PasswordEncoder passwordEncoder,
                                             UserDetailsService userDetailsServiceImpl,
                                             AuthenticationManager authenticationManagerBean,
                                             TokenStore jwtAccessTokenStore,
                                             JwtAccessTokenConverter jwtAccessTokenConverter,
                                             TokenEnhancer myOAuth2AccessTokenEnhancer) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.authenticationManagerBean = authenticationManagerBean;
        this.jwtAccessTokenStore = jwtAccessTokenStore;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        this.myOAuth2AccessTokenEnhancer = myOAuth2AccessTokenEnhancer;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = new ArrayList<>();
        enhancerList.add(myOAuth2AccessTokenEnhancer);
        enhancerList.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(enhancerList);

        endpoints.userDetailsService(userDetailsServiceImpl)
            .authenticationManager(authenticationManagerBean)
            // token 存放的位置，生产放到 redis/jwt/jwk/db 中
            //.tokenStore(new InMemoryTokenStore());
            .tokenStore(jwtAccessTokenStore)
            .accessTokenConverter(jwtAccessTokenConverter)
            .tokenEnhancer(tokenEnhancerChain);
    }

    /**
     * http://localhost:30020/oauth/authorize?response_type=code&client_id=admin_clientId&scop=all&redirect_uri=http://www.baidu.com
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient("admin_clientId")
            .secret(passwordEncoder.encode("224466"))
            .redirectUris("http://127.0.0.1:30040/login")
            .autoApprove(true)
            .scopes("all")
            // 访问令牌失效时间
            .accessTokenValiditySeconds(36000)
            .authorizedGrantTypes("authorization_code", "password", "refresh_token");

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("isAuthenticated()");
    }
}
