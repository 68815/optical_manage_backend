package com.optical.manage.mapper;

import com.optical.manage.DO.Routing;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface RoutingMapper extends BaseMapper<Routing> {

    @Select("SELECT routing_id, name, start_point_id, end_point_id, type, length, cables_count, " +
            "ST_AsText(geom) AS geom, props, created_at, updated_at " +
            "FROM routing " +
            "WHERE routing_id = #{id}")
    Routing getById(Long id);

    @Insert("INSERT INTO routing (name, start_point_id, end_point_id, type, length, cables_count, geom, props) " +
            "VALUES (#{name}, #{startPointId}, #{endPointId}, #{type}, #{length}, #{cablesCount}, ST_GeomFromText(#{geom}, 4326), #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "routingId")
    Long insertRouting(Routing routing);

    @Update("<script>" +
            "UPDATE routing " +
            "SET name = #{name}, " +
            "start_point_id = #{startPointId}, " +
            "end_point_id = #{endPointId}, " +
            "type = #{type}, " +
            "length = #{length}, " +
            "cables_count = #{cablesCount}, " +
            "geom = ST_GeomFromText(#{geom}, 4326), " +
            "props = #{props}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE routing_id = #{routingId}" +
            "</script>")
    int updateRouting(Routing routing);

    @Delete("DELETE FROM routing WHERE routing_id = #{id}")
    int deleteById(Long id);

    @Select("<script>" +
            "SELECT routing_id, name, start_point_id, end_point_id, type, length, cables_count, " +
            "ST_AsText(geom) AS geom, props " +
            "FROM routing " +
            "WHERE 1=1 " +
            "<if test='minLng != null'>" +
            "AND geom &amp;&amp; ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326) " +
            "</if>" +
            "<if test='limit != null'>LIMIT #{limit}</if>" +
            "</script>")
    List<Routing> selectByBbox(@Param("minLng") Double minLng, @Param("minLat") Double minLat,
                               @Param("maxLng") Double maxLng, @Param("maxLat") Double maxLat,
                               @Param("limit") Integer limit);
}
