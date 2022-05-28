package com.qing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qing.config.response.NotResultWrap;
import com.qing.config.response.Result;
import com.qing.config.response.ResultCode;
import com.qing.config.security.pojo.JwtUser;
import com.qing.config.security.pojo.Users;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @date 2022/5/11 13:20
 * @author qinghua
 */
@RestController
public class HelloController {

    @NotResultWrap
    @GetMapping("/test02")
    public String test02(@AuthenticationPrincipal JwtUser user) throws JsonProcessingException {
        System.out.println(user);
        return new ObjectMapper().writeValueAsString(Result.failure(ResultCode.TIMEOUT,"长时间未操作，请重新登录"));
    }

    /**
     * 测试注解 @CurrentSecurityContext
     * @param str  json字符串
     * @param principal  这个会输出
     * @return {@link String }
     * @author qinghua
     * @date 2022/5/12 19:29
     */
    @PostMapping("/hello")
    public String test(@RequestBody String str, HttpServletRequest httpServletRequest,
                       @CurrentSecurityContext(expression = "authentication.principal") Principal principal) {
        System.out.println(str);
        System.out.println(principal);
        return "hello World";
    }

    @GetMapping("/index")
    public String index() {
        System.out.println("hello index");
        return "hello index";
    }

    @GetMapping("/update")
    public String update() {
        return "hello update";
    }

}
