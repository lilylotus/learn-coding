package cn.nihility.security.oauth2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author orchid
 * @date 2021-05-10 23:31:37
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final static Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 认证，自定义登录配置
        // 表单方式登录
        http.formLogin();

        // 授权
        http.authorizeRequests()
            .antMatchers("/login.html", "/favicon.ico", "/success.html", "/failure.html").permitAll()
            .antMatchers("/oauth/**", "/login/**", "/logout/**").permitAll()
            .anyRequest().authenticated();

        // 关闭 csrf
        http.csrf().disable();

        http.sessionManagement().disable();

        // 退出登录
        http.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login.html");

        http.sessionManagement().disable();

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
