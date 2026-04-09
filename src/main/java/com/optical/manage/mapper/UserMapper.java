package com.optical.manage.mapper;

import com.optical.manage.DO.User;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
