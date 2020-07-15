package cn.nihility.jwt.controller;

import cn.nihility.jwt.models.AuthenticationRequest;
import cn.nihility.jwt.models.AuthenticationResponse;
import cn.nihility.jwt.services.MyUserDetailsService;
import cn.nihility.jwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloResource
 *
 * @author dandelion
 * @date 2020-03-24 19:54
 */
@RestController
@RequestMapping({"/resource"})
public class HelloResource {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping({"/hello"})
    public String hello() {
        return "Hello World";
    }

    @RequestMapping(value = {"/authenticate"}, method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        System.out.println("Authenticate");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getName(),
                            authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new Exception("Incorrect username or password.", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getName());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }


}
