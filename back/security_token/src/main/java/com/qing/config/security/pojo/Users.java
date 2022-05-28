package com.qing.config.security.pojo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author 竹秋廿四
 */
@Data
public class Users {

    protected Integer id;

    private String username;

    private String password;

    protected String pic;

}
