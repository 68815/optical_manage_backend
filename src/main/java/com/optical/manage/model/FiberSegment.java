package com.optical.manage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resources")
public class FiberSegment {
    @TableId(type = IdType.AUTO)
    private Long resourcesId;
    private String type; // 固定为 fiber_segment
    private String geom; // 空间位置，以 WKT 格式存储（LINESTRING）
    private String props; // 属性字段，以 JSON 字符串存储
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}