package com.qing.config.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qing.config.security.mapper.RolesMapper;
import com.qing.config.security.mapper.UserRoleMapper;
import com.qing.config.security.mapper.UsersMapper;
import com.qing.config.security.pojo.JwtUser;
import com.qing.config.security.pojo.Roles;
import com.qing.config.security.pojo.UserRole;
import com.qing.config.security.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 竹秋廿四
 */
@Service("UserDetailServiceImpl")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    RolesMapper rolesMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = usersMapper.login(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        return new JwtUser(user, getAuthorities(user.getId()));

    }

    /**
     * 获得访问角色权限
     */
    public Collection<GrantedAuthority> getAuthorities(Integer userId) {

        // 查询user对应的role
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);

        // 绑定权限
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            Roles role = rolesMapper.selectById(userRole.getRoleId());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        }

        //最终这里返回的是用户权限集合
        return authorities;
    }
}
