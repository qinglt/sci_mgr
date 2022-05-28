package com.qing;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qing.config.redis.RedisUtil;
import com.qing.config.security.mapper.BlackUserMapper;
import com.qing.config.security.pojo.BlackUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * @author 竹秋廿四
 */
@SpringBootApplication
public class SecurityTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityTokenApplication.class, args);
    }

}
