package com.optical.manage.mapper;

import com.optical.manage.DO.Resource;
import com.optical.manage.dto.nlq.ParsedQuery;

import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
    
    @Select("<script>" +
            "SELECT resources_id, type, ST_AsText(geom) AS geom, props, created_at, updated_at " +
            "FROM resources " +
            "WHERE resources_id = #{id}" +
            "</script>")
    Resource getById(Long id);
    
    @Insert("<script>" +
            "INSERT INTO resources (type, geom, props) " +
            "VALUES (#{type}, ST_SetSRID(ST_MakePoint(#{lng}, #{lat}), 4326), #{props})" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "resourcesId")
    int insertResource(@Param("type") String type, @Param("lat") double lat, @Param("lng") double lng, @Param("props") String props);
    
    @Insert("<script>" +
            "INSERT INTO resources (type, geom, props) " +
            "VALUES ('fiber_segment', ST_SetSRID(ST_MakeLine(" +
            "<foreach item='point' collection='points' separator=','>" +
            "ST_MakePoint(#{point.lng}, #{point.lat})" +
            "</foreach>" +
            "), 4326), #{props})" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFiberSegment(@Param("points") List<Map<String, Double>> points, @Param("props") String props);
    
    @Update("<script>" +
            "UPDATE resources " +
            "SET type = #{type}, " +
            "geom = ST_SetSRID(ST_MakePoint(#{lng}, #{lat}), 4326), " +
            "props = #{props}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE resources_id = #{id}" +
            "</script>")
    int updateResource(@Param("id") Long id, @Param("type") String type, @Param("lat") double lat, @Param("lng") double lng, @Param("props") String props);
    
    @Update("<script>" +
            "UPDATE resources " +
            "SET geom = ST_SetSRID(ST_MakeLine(" +
            "<foreach item='point' collection='points' separator=','>" +
            "ST_MakePoint(#{point.lng}, #{point.lat})" +
            "</foreach>" +
            "), 4326), " +
            "props = #{props}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE resources_id = #{id}" +
            "</script>")
    int updateFiberSegment(@Param("id") Long id, @Param("points") List<Map<String, Double>> points, @Param("props") String props);
    
    @Delete("DELETE FROM resources WHERE resources_id = #{id}")
    int deleteById(Long id);
    
    @Select("<script>" +
            "SELECT resources_id, type, ST_X(geom) AS lng, ST_Y(geom) AS lat, props " +
            "FROM resources " +
            "WHERE 1=1 " +
            "<if test='resourceTypes != null and resourceTypes.size() > 0'>" +
            "AND type IN " +
            "<foreach item='type' collection='resourceTypes' open='(' separator=',' close=')'>" +
            "#{type}" +
            "</foreach>" +
            "</if>" +
            "<if test='geo != null and geo.type == \"radius\" and geo.center != null'>" +
            "AND ST_DWithin(geom::geography, " +
            "ST_SetSRID(ST_MakePoint(#{geo.center.lng}, #{geo.center.lat}), 4326)::geography, " +
            "#{geo.radiusM})" +
            "</if>" +
            "<if test=\"geo != null and geo.type == \'bbox\'\">" +
            "AND geom &amp;&amp; ST_MakeEnvelope(#{geo.minLng}, #{geo.minLat}, #{geo.maxLng}, #{geo.maxLat}, 4326)" +
            "</if>" +
            "LIMIT #{limit}" +
            "</script>")
    List<Map<String, Object>> searchByGeo(@Param("resourceTypes") List<String> resourceTypes,
                                          @Param("geo") ParsedQuery.GeoConstraint geo,
                                          @Param("limit") Integer limit);
    
    @Select("<script>" +
            "SELECT resources_id, type, ST_X(geom) AS lng, ST_Y(geom) AS lat, props " +
            "FROM resources " +
            "WHERE 1=1 " +
            "<if test='resourceTypes != null and resourceTypes.size() > 0'>" +
            "AND type IN " +
            "<foreach item='type' collection='resourceTypes' open='(' separator=',' close=')'>" +
            "#{type}" +
            "</foreach>" +
            "</if>" +
            "<if test='filters != null'>" +
            "<foreach item='value' index='key' collection='filters'>" +
            "AND props-&gt;&gt;#{key} = #{value}" +
            "</foreach>" +
            "</if>" +
            "LIMIT #{limit}" +
            "</script>")
    List<Map<String, Object>> searchByFilters(@Param("resourceTypes") List<String> resourceTypes,
                                              @Param("filters") Map<String, Object> filters,
                                              @Param("limit") Integer limit);
    
    @Select("<script>" +
            "SELECT COUNT(*) as count " +
            "FROM resources " +
            "WHERE 1=1 " +
            "<if test='resourceTypes != null and resourceTypes.size() > 0'>" +
            "AND type IN " +
            "<foreach item='type' collection='resourceTypes' open='(' separator=',' close=')'>" +
            "#{type}" +
            "</foreach>" +
            "</if>" +
            "<if test='geo != null and geo.type == \"radius\" and geo.center != null'>" +
            "AND ST_DWithin(geom::geography, " +
            "ST_SetSRID(ST_MakePoint(#{geo.center.lng}, #{geo.center.lat}), 4326)::geography, " +
            "#{geo.radiusM})" +
            "</if>" +
            "<if test=\"geo != null and geo.type == \'bbox\'\">" +
            "AND geom &amp;&amp; ST_MakeEnvelope(#{geo.minLng}, #{geo.minLat}, #{geo.maxLng}, #{geo.maxLat}, 4326)" +
            "</if>" +
            "</script>")
    Long countByGeo(@Param("resourceTypes") List<String> resourceTypes,
                    @Param("geo") ParsedQuery.GeoConstraint geo);
    
    @Select("<script>" +
            "SELECT resources_id, type, ST_AsText(geom) AS geom, props " +
            "FROM resources " +
            "WHERE type = 'fiber_segment' " +
            "<if test='bbox != null'>" +
            "AND geom &amp;&amp; ST_MakeEnvelope(#{bbox.minLng}, #{bbox.minLat}, #{bbox.maxLng}, #{bbox.maxLat}, 4326)" +
            "</if>" +
            "</script>")
    List<Map<String, Object>> getFiberSegments(@Param("bbox") Map<String, Double> bbox);
}