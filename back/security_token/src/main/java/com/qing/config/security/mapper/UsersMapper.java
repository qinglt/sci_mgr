package com.qing.config.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qing.config.security.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 竹秋廿四
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

    @Select("select id, username, password, pic from users where username = #{username}")
    Users login(@Param("username") String username);

}
