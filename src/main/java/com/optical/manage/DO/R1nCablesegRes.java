package com.optical.manage.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@TableName("r_1n_cableseg_res")
public class R1nCablesegRes {
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    @TableField("segment_id")
    private Long segmentId;
    @TableField("res_id")
    private Long resId;
    @TableField("res_connector_id")
    private Long resConnectorId;
    @TableField("sequence")
    private Integer sequence;
    @TableField("props")
    private String props;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
