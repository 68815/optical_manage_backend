package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@TableName("fiber_segment")
public class FiberSegment {
    @TableId(type = IdType.AUTO)
    private Long segmentId;
    @TableField("length")
    private Double length; // 光缆段长度
    @TableField("start_point_id")
    private Long startPointId; // 起点资源点ID
    @TableField("end_point_id")
    private Long endPointId; // 终点资源点ID
    @TableField("geom")
    private String geom;// 空间位置，以 WKT 格式存储（LINESTRING）
    @TableField("props")
    private String props;// 属性字段，以 JSON 字符串存储
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
