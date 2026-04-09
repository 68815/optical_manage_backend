package com.optical.manage.mapper;

import com.optical.manage.DO.R1nCableCableseg;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface R1nCableCablesegMapper extends BaseMapper<R1nCableCableseg> {

    @Select("SELECT id, cable_id, segment_id, sequence, props, created_at " +
            "FROM r_1n_cable_cableseg " +
            "WHERE id = #{id}")
    R1nCableCableseg getById(Long id);

    @Insert("INSERT INTO r_1n_cable_cableseg (cable_id, segment_id, sequence, props) " +
            "VALUES (#{cableId}, #{segmentId}, #{sequence}, #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRelation(R1nCableCableseg relation);

    @Delete("DELETE FROM r_1n_cable_cableseg WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, cable_id, segment_id, sequence, props, created_at " +
            "FROM r_1n_cable_cableseg " +
            "WHERE cable_id = #{cableId} " +
            "ORDER BY sequence")
    List<R1nCableCableseg> getByCableId(Long cableId);

    @Select("SELECT id, cable_id, segment_id, sequence, props, created_at " +
            "FROM r_1n_cable_cableseg " +
            "WHERE segment_id = #{segmentId}")
    List<R1nCableCableseg> getBySegmentId(Long segmentId);
}
