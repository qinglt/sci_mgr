package com.qing.config.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author qinghua
 * @date 2021/8/5 22:28
 */
@Slf4j
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    @Value("${jwt.expiration}")
    private Integer JWT_TOKEN_VALIDITY;

    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * 从token中获取用户名
     * @param token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取服务器名
     * @param token
     */
    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    /**
     * 生成JWT
     * @param userId  用户id
     * @param username  用户名
     * @param auth 用户权限
     */
    public String generateToken(Integer userId, String username, String auth) {
        /*iss: 该JWT的签发者
          sub: 该JWT所面向的用户
          aud: 接收该JWT的一方
          exp(expires): 什么时候过期，这里是一个Unix时间戳
          iat(issued at): 在什么时候签发的*/
        return Jwts.builder()
                .claim("auth", auth)
                .setId(String.valueOf(userId))
                .setSubject(username)
                .setAudience("qinghua")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 验证token的sub和数据库中的username是否一致
     * @param token
     * @param userDetails
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 获取token携带的声明
     * @param token
     */
    public Claims getAllClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("====================非法token====================");
        }
        return claims;
    }

    /**
     * 判断是否过期
     * @param token
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 获取过期时间
     * @param token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 根据声明生成token
     * @param claims 声明
     */
    private String doGenerateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }
}

