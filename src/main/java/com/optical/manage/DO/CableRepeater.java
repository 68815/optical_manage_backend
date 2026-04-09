package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;

@Data
@TableName("cable_repeater")
public class CableRepeater {
    @TableId(type = IdType.AUTO, value = "repeater_id")
    private Long repeaterId;
    @TableField("name")
    private String name;
    @TableField("start_point_id")
    private Long startPointId;
    @TableField("end_point_id")
    private Long endPointId;
    @TableField("repeater_point_id")
    private Long repeaterPointId;
    @TableField("length")
    private BigDecimal length;
    @TableField("loss_budget")
    private BigDecimal lossBudget;
    @TableField("status")
    private Integer status;
    @TableField("props")
    private String props;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
