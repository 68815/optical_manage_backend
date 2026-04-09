package com.optical.manage.mapper;

import com.optical.manage.DO.R1nRoutingCableseg;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface R1nRoutingCablesegMapper extends BaseMapper<R1nRoutingCableseg> {

    @Select("SELECT id, routing_id, segment_id, props, created_at " +
            "FROM r_1n_routing_cableseg " +
            "WHERE id = #{id}")
    R1nRoutingCableseg getById(Long id);

    @Insert("INSERT INTO r_1n_routing_cableseg (routing_id, segment_id, props) " +
            "VALUES (#{routingId}, #{segmentId}, #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRelation(R1nRoutingCableseg relation);

    @Delete("DELETE FROM r_1n_routing_cableseg WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, routing_id, segment_id, props, created_at " +
            "FROM r_1n_routing_cableseg " +
            "WHERE routing_id = #{routingId}")
    List<R1nRoutingCableseg> getByRoutingId(Long routingId);

    @Select("SELECT id, routing_id, segment_id, props, created_at " +
            "FROM r_1n_routing_cableseg " +
            "WHERE segment_id = #{segmentId}")
    List<R1nRoutingCableseg> getBySegmentId(Long segmentId);
}
