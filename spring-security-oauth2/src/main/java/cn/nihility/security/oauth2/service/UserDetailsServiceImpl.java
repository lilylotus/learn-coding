package cn.nihility.security.oauth2.service;

import cn.nihility.security.oauth2.pojo.SecurityUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author orchid
 * @date 2021-05-10 23:35:53
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final static Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("自定义用户 [{}] 登录逻辑", username);
        if (null == username || "".equals(username)) {
            throw new UsernameNotFoundException("自定义用户登录逻辑用户名为空");
        }

        if ("admin".equals(username)) {
            return new SecurityUserDetails(username, passwordEncoder.encode("123456"),
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal,success"));
        } else if ("normal".equals(username)) {
            return new SecurityUserDetails(username, passwordEncoder.encode("123456"),
                AuthorityUtils.commaSeparatedStringToAuthorityList("success"));
        }

        throw new UsernameNotFoundException("自定义用户登录逻辑用户不存在");

    }

}
