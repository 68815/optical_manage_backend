package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@TableName("r_1n_cable_cableseg")
public class R1nCableCableseg {
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    @TableField("cable_id")
    private Long cableId;
    @TableField("segment_id")
    private Long segmentId;
    @TableField("sequence")
    private Integer sequence;
    @TableField("props")
    private String props;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
