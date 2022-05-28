package com.qing.config.security.filter;

import com.qing.config.redis.RedisUtil;
import com.qing.config.security.pojo.JwtUser;
import com.qing.config.security.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: springboot_demo
 * @description: Token拦截检查
 * @author: ChenZiYang
 * @create: 2020-10-09 13:53
 **/
@Slf4j
@Component
public class TokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    RedisUtil redisUtil;

    @Value("${jwt.tokenKey}")
    private String TOKEN_KEY;

    @Value("${jwt.tokenHead}")
    private String TOKEN_HEAD;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(TOKEN_KEY);

        // 不符合则以匿名用户访问
        if (!StringUtils.isEmpty(token) && token.startsWith(TOKEN_HEAD)) {
            String jwtToken = token.substring(TOKEN_HEAD.length() + 1);
            // 判断是否过期
            if (!jwtTokenUtil.isTokenExpired(jwtToken)) {
                // 验证token
                if (jwtTokenUtil.getAllClaimsFromToken(jwtToken) != null) {
                    String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 从token中获取用户信息
                        Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
                        UserDetails userDetails = new JwtUser(Integer.parseInt(claims.getId()), username, "123456", AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("auth").toString()));
                        //保存用户信息和权限信息
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // After setting the Authentication in the context, we specify that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } else {
                    // token无效则设置状态码为400
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"code\":400,\"msg\":\"Not logged in\",\"obj\":\"未登录\"}");
                    return;
                }
            } else {
                // token过期则设置状态码为599
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":599,\"msg\":\"timeout\",\"obj\":\"长时间未操作，请重新登录\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
