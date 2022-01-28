package cn.nihility.security.oauth2.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64Codec;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author orchid
 * @date 2021-05-12 23:58:58
 */
public class JwtTest {

    @Test
    public void testJwtBuilder() {
        JwtBuilder jwtBuilder = Jwts.builder()
            // 设置 ID : {"jti": 888}
            .setId("888")
            // 签发用户 {"sub": "Lotus"}
            .setSubject("Lotus")
            // 设置签发时间 {"iat": "xxx"}
            .setIssuedAt(new Date())
            // 指定签名算法和 Salt (加密盐)
            .signWith(SignatureAlgorithm.HS256, "secretKey")
            // 1 分钟过期 {"exp": "xxx"}
            .setExpiration(new Date(System.currentTimeMillis() + (60 * 10000)))
            // 添加自定义负载
            .claim("logo", "xxx.jpg")
            .claim("username", "claimUserName");

        // 生存 Token
        String token = jwtBuilder.compact();
        System.out.println(token);

        String[] split = token.split("\\.");
        // 头部
        System.out.println(new String(Base64Codec.BASE64.decode(split[0]), StandardCharsets.UTF_8));
        // 负载
        System.out.println(new String(Base64Codec.BASE64.decode(split[1]), StandardCharsets.UTF_8));
        // 签名
        System.out.println(new String(Base64Codec.BASE64.decode(split[2]), StandardCharsets.UTF_8));

    }

    @Test
    public void testParseToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiJMb3R1cyIsImlhdCI6MTYyMDgzNjQ5OCwiZXhwIjoxNjIwODM3MDk4LCJsb2dvIjoieHh4LmpwZyIsInVzZXJuYW1lIjoiY2xhaW1Vc2VyTmFtZSJ9.Vcj-MhYzI1xmgBXLxI0G5c5HjGyh2hU6WFKd3hZjCf8";

        Jws<Claims> jws = Jwts.parser().setSigningKey("secretKey")
            .parseClaimsJws(token);

        Claims bodyClaims = jws.getBody();
        JwsHeader header = jws.getHeader();
        String signature = jws.getSignature();

        System.out.println(header.getAlgorithm());
        System.out.println(header.getKeyId());

        System.out.println(signature);

        System.out.println("logo = " + bodyClaims.get("logo"));
        System.out.println("username = " + bodyClaims.get("username"));

        System.out.println("--------- body");
        System.out.println(bodyClaims.getId());
        System.out.println(bodyClaims.getSubject());

        Date issuedAt = bodyClaims.getIssuedAt();
        Date expiration = bodyClaims.getExpiration();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(dateFormat.format(issuedAt));
        System.out.println(dateFormat.format(expiration));

    }

}
