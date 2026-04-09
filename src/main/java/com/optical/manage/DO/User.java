package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO, value = "user_id")
    private Long userId;
    @TableField("name")
    private String name;
    @TableField("password")
    private String password;
    @TableField("phone")
    private String phone;
    @TableField("email")
    private String email;
    @TableField("avatar_url")
    private String avatarUrl;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
