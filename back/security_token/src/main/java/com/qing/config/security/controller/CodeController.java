package com.qing.config.security.controller;

import com.qing.config.redis.RedisUtil;
import com.qing.config.response.NotResultWrap;
import com.qing.config.security.utils.ValidateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/verifyCode")
public class CodeController {

    // 设置验证码过期时间（单位：秒）
    private static final Integer CODE_TIMEOUT = 120;

    @Autowired
    private RedisUtil redisUtil;

    @NotResultWrap
    @RequestMapping("/getCode/{num}")
    public void getCode(@PathVariable("num") String num, HttpServletResponse response) throws IOException {

        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        ValidateCode vCode = new ValidateCode(100, 35, 4, 100);

        // 获取id，如果不存在就创建
        redisUtil.set(num, vCode.getCode(), CODE_TIMEOUT);

        vCode.write(response.getOutputStream());

    }

    @GetMapping("/comparePicCode/{code}/{num}")
    public String comparePicCode(@PathVariable("code") String userCode,
                                 @PathVariable("num") String num) {
        if (redisUtil.hasKey(num)){
            String code = (String) redisUtil.get(num);
            if (userCode.equalsIgnoreCase(code)){
                return "正确";
            } else {
                return "错误";
            }
        }else {
            return "过期";
        }

    }
}