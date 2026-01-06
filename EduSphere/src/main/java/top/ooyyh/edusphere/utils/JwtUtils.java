package top.ooyyh.edusphere.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {

    // 生成秘钥
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 有效期 1 小时
    private static final long EXPIRATION = 1000 * 60 * 60;

    // 生成 token
    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)  // 设置主题，一般用用户名
                .claim("role", role)   // 携带角色
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 过期时间
                .signWith(key) // 签名
                .compact();
    }

    // 解析 token，获取用户名
    public static String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 获取角色
    public static String getRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }
}
