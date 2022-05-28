package com.qing.config.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.config.security.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}