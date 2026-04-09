package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;

@Data
@TableName("cable")
public class Cable {
    @TableId(type = IdType.AUTO, value = "cable_id")
    private Long cableId;
    @TableField("name")
    private String name;
    @TableField("cable_level")
    private String cableLevel;
    @TableField("start_point_id")
    private Long startPointId;
    @TableField("end_point_id")
    private Long endPointId;
    @TableField("total_length")
    private BigDecimal totalLength;
    @TableField("total_fiber_count")
    private Integer totalFiberCount;
    @TableField("operator")
    private String operator;
    @TableField("status")
    private Integer status;
    @TableField("props")
    private String props;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
