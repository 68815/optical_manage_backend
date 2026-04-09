package com.optical.manage.mapper;

import com.optical.manage.DO.R1nCablesegRes;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface R1nCablesegResMapper extends BaseMapper<R1nCablesegRes> {

    @Select("SELECT id, segment_id, res_id, res_connector_id, sequence, props, created_at " +
            "FROM r_1n_cableseg_res " +
            "WHERE id = #{id}")
    R1nCablesegRes getById(Long id);

    @Insert("INSERT INTO r_1n_cableseg_res (segment_id, res_id, res_connector_id, sequence, props) " +
            "VALUES (#{segmentId}, #{resId}, #{resConnectorId}, #{sequence}, #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRelation(R1nCablesegRes relation);

    @Delete("DELETE FROM r_1n_cableseg_res WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, segment_id, res_id, res_connector_id, sequence, props, created_at " +
            "FROM r_1n_cableseg_res " +
            "WHERE segment_id = #{segmentId} " +
            "ORDER BY sequence")
    List<R1nCablesegRes> getBySegmentId(Long segmentId);

    @Select("SELECT id, segment_id, res_id, res_connector_id, sequence, props, created_at " +
            "FROM r_1n_cableseg_res " +
            "WHERE res_id = #{resId}")
    List<R1nCablesegRes> getByResId(Long resId);
}
