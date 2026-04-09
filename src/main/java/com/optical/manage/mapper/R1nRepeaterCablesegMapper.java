package com.optical.manage.mapper;

import com.optical.manage.DO.R1nRepeaterCableseg;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface R1nRepeaterCablesegMapper extends BaseMapper<R1nRepeaterCableseg> {

    @Select("SELECT id, repeater_id, segment_id, sequence, props, created_at " +
            "FROM r_1n_repeater_cableseg " +
            "WHERE id = #{id}")
    R1nRepeaterCableseg getById(Long id);

    @Insert("INSERT INTO r_1n_repeater_cableseg (repeater_id, segment_id, sequence, props) " +
            "VALUES (#{repeaterId}, #{segmentId}, #{sequence}, #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRelation(R1nRepeaterCableseg relation);

    @Delete("DELETE FROM r_1n_repeater_cableseg WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, repeater_id, segment_id, sequence, props, created_at " +
            "FROM r_1n_repeater_cableseg " +
            "WHERE repeater_id = #{repeaterId} " +
            "ORDER BY sequence")
    List<R1nRepeaterCableseg> getByRepeaterId(Long repeaterId);

    @Select("SELECT id, repeater_id, segment_id, sequence, props, created_at " +
            "FROM r_1n_repeater_cableseg " +
            "WHERE segment_id = #{segmentId}")
    List<R1nRepeaterCableseg> getBySegmentId(Long segmentId);
}
