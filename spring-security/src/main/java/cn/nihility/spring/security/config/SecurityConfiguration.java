package cn.nihility.spring.security.config;

import cn.nihility.spring.security.handler.SecurityAccessDeniedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * @author orchid
 * @date 2021-05-10 23:31:37
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final static Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    private UserDetailsService userDetailsServiceImpl;

    @Autowired
    public void setUserDetailsServiceImpl(UserDetailsService userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 认证，自定义登录配置
        // 表单方式登录
        http.formLogin()
            // 自定义登录页面
            //.loginPage("/login.html")
            // thymeleaf login page, open csrf
            .loginPage("/toLogin")
            // 登录逻辑处理 URL
            .loginProcessingUrl("/login")
            // 自定登录成功跳转 url， POST
            .successForwardUrl("/toSuccess")
            // 自定义登录失败跳转 url， POST
            .failureForwardUrl("/toFailure")
            // 自定义登录表单密码参数名称
            .passwordParameter("password")
            // 自定义登录表单用户名参数名称
            .usernameParameter("username");

        // 授权
        http.authorizeRequests()
            .antMatchers("/login.html", "/toLogin", "/logout", "/favicon.ico").permitAll()
            .antMatchers("/success.html", "/failure.html").hasAuthority("success")
            .anyRequest().authenticated();

        // 关闭 csrf
        http.csrf().disable();
        //http.cors().disable();

        // 记住我
        http.rememberMe()
            .rememberMeParameter("remember-me")
            // 自定义登录逻辑
            .userDetailsService(userDetailsServiceImpl)
            // 登录 token 保存
            .tokenRepository(persistentTokenRepository());

        // 退出登录
        http.logout()
            .logoutUrl("/logout")
            .deleteCookies("JSESSIONID")
            .logoutSuccessUrl("/toLogin");

        // 异常处理
        http.exceptionHandling().accessDeniedHandler(new SecurityAccessDeniedHandler());

    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // JdbcTokenRepositoryImpl
        /*JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 启动是自动创建表，第一次开启，后面关掉
        tokenRepository.setCreateTableOnStartup(true);*/
        return new InMemoryTokenRepositoryImpl();
    }

}
