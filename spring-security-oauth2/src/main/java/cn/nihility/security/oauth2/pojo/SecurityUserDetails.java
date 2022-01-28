package cn.nihility.security.oauth2.pojo;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author orchid
 * @date 2021-05-10 23:45:27
 */
public class SecurityUserDetails implements UserDetails, CredentialsContainer {

    private String username;
    private String password;
    private List<GrantedAuthority> grantedAuthorityList;

    public SecurityUserDetails(String username, String password, List<GrantedAuthority> grantedAuthorityList) {
        this.username = username;
        this.password = password;
        this.grantedAuthorityList = grantedAuthorityList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public void eraseCredentials() {

    }

    @Override
    public String toString() {
        return "{" +
            "username='" + username + '\'' +
            '}';
    }

}
