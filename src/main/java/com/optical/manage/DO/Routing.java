package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;

@Data
@TableName("routing")
public class Routing {
    @TableId(type = IdType.AUTO, value = "routing_id")
    private Long routingId;
    @TableField("name")
    private String name;
    @TableField("start_point_id")
    private Long startPointId;
    @TableField("end_point_id")
    private Long endPointId;
    @TableField("type")
    private String type;
    @TableField("length")
    private BigDecimal length;
    @TableField("cables_count")
    private Integer cablesCount;
    @TableField("geom")
    private String geom;
    @TableField("props")
    private String props;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
