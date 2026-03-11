package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@TableName("resource_point")
public class Resource {
    @TableId(type = IdType.AUTO)
    private Long resourcesId;
    @TableField("type")
    private String type; // 资源类型：pole, manhole, office, cabinet, base_station 等
    @TableField("name")
    private String name; // 资源名称
    @TableField("address")
    private String address; // 资源地址
    @TableField("status")
    private String status; // 资源状态
    @TableField("geom")
    private String geom; // 空间位置，以 WKT 格式存储
    @TableField("props")
    private String props; // 属性字段，以 JSON 字符串存储
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
