package com.qing.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qing.config.redis.RedisUtil;
import com.qing.config.security.pojo.JwtUser;
import com.qing.config.security.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qinghua
 * @date 2021/5/12 22:16
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        // 取得账号信息
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();

        // 回调信息
        Map<String, Object> data = new HashMap<>();
        data.put("username", userDetails.getUsername());
        data.put("roleList", userDetails.getAuthorities());
        // 将权限集合转成String
        StringBuilder auth = new StringBuilder();
        userDetails.getAuthorities().stream().forEach(item -> {
            auth.append(item);
            auth.append(",");
        });
        // 用户本次登录的token令牌
        data.put("token", "Bearer " + jwtTokenUtil.generateToken(userDetails.getId(), userDetails.getUsername(), auth.toString()));

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write("{\"code\":200,\"msg\":\"success\",\"obj\":" + new ObjectMapper().writeValueAsString(data) + "}");

    }
}
