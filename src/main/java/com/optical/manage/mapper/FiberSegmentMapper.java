package com.optical.manage.mapper;

import com.optical.manage.DO.FiberSegment;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface FiberSegmentMapper extends BaseMapper<FiberSegment> {

    @Select("SELECT segment_id, name, start_point_id, end_point_id, routing_id, cable_level, " +
            "length, fiber_count, tube_count, fibers_per_tube, laying_style, " +
            "ST_AsText(geom) AS geom, props, created_at, updated_at " +
            "FROM fiber_segment " +
            "WHERE segment_id = #{id}")
    FiberSegment getById(Long id);

    @Insert("INSERT INTO fiber_segment (name, start_point_id, end_point_id, routing_id, cable_level, " +
            "length, fiber_count, tube_count, fibers_per_tube, laying_style, geom, props) " +
            "VALUES (#{name}, #{startPointId}, #{endPointId}, #{routingId}, #{cableLevel}, " +
            "#{length}, #{fiberCount}, #{tubeCount}, #{fibersPerTube}, #{layingStyle}, ST_GeomFromText(#{geom}, 4326), #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "segmentId")
    Long insertSegment(FiberSegment fiberSegment);

    @Update("<script>" +
            "UPDATE fiber_segment " +
            "SET name = #{name}, " +
            "start_point_id = #{startPointId}, " +
            "end_point_id = #{endPointId}, " +
            "routing_id = #{routingId}, " +
            "cable_level = #{cableLevel}, " +
            "length = #{length}, " +
            "fiber_count = #{fiberCount}, " +
            "tube_count = #{tubeCount}, " +
            "fibers_per_tube = #{fibersPerTube}, " +
            "laying_style = #{layingStyle}, " +
            "geom = ST_GeomFromText(#{geom}, 4326), " +
            "props = #{props}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE segment_id = #{segmentId}" +
            "</script>")
    int updateSegment(FiberSegment fiberSegment);

    @Delete("DELETE FROM fiber_segment WHERE segment_id = #{id}")
    int deleteById(Long id);

    @Select("<script>" +
            "SELECT segment_id, name, start_point_id, end_point_id, routing_id, cable_level, " +
            "length, fiber_count, tube_count, fibers_per_tube, laying_style, " +
            "ST_AsText(geom) AS geom, props " +
            "FROM fiber_segment " +
            "WHERE 1=1 " +
            "<if test='minLng != null'>" +
            "AND geom &amp;&amp; ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326) " +
            "</if>" +
            "<if test='limit != null'>LIMIT #{limit}</if>" +
            "</script>")
    List<FiberSegment> selectByBbox(@Param("minLng") Double minLng, @Param("minLat") Double minLat,
                                    @Param("maxLng") Double maxLng, @Param("maxLat") Double maxLat,
                                    @Param("limit") Integer limit);
}
