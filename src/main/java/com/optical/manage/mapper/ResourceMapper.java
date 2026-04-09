package com.optical.manage.mapper;

import com.optical.manage.DO.Resource;

import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    @Select("SELECT resource_point_id, type, name, address, status, ST_AsText(geom) AS geom, props, created_at, updated_at " +
            "FROM resource_point " +
            "WHERE resource_point_id = #{id}")
    Resource getById(Long id);

    @Insert("INSERT INTO resource_point (name, type, address, status, geom, props) " +
            "VALUES (#{name}, #{type}::resource_type, #{address}, #{status}, ST_GeomFromText(#{geom}, 4326), #{props, typeHandler=com.optical.manage.handler.JsonbTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "resourcePointId")
    Long insertResource(Resource resource);

    @Update("<script>" +
            "UPDATE resource_point " +
            "SET name = #{name}, " +
            "type = #{type}::resource_type, " +
            "address = #{address}, " +
            "status = #{status}, " +
            "geom = ST_GeomFromText(#{geom}, 4326), " +
            "props = #{props, typeHandler=com.optical.manage.handler.JsonbTypeHandler}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE resource_point_id = #{resourcePointId}" +
            "</script>")
    int updateResource(Resource resource);

    @Delete("DELETE FROM resource_point WHERE resource_point_id = #{id}")
    int deleteResourceById(Long id);

    @Select("<script>" +
            "SELECT resource_point_id, type, name, address, status, ST_X(geom) AS lng, ST_Y(geom) AS lat, props " +
            "FROM resource_point " +
            "WHERE 1=1 " +
            "<if test='resourceTypes != null and resourceTypes.size() > 0'>" +
            "AND type::text IN " +
            "<foreach item='type' collection='resourceTypes' open='(' separator=',' close=')'>" +
            "#{type}" +
            "</foreach>" +
            "</if>" +
            "<if test='filters != null'>" +
            "<foreach item='value' index='key' collection='filters'>" +
            "AND props->>#{key} = #{value}" +
            "</foreach>" +
            "</if>" +
            "<if test='minLng != null'>" +
            "AND geom &amp;&amp; ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326) " +
            "</if>" +
            "<if test='limit != null'>LIMIT #{limit}</if>" +
            "</script>")
    List<Map<String, Object>> searchByFilters(@Param("resourceTypes") List<String> resourceTypes,
                                              @Param("filters") Map<String, Object> filters,
                                              @Param("minLng") Double minLng, @Param("minLat") Double minLat,
                                              @Param("maxLng") Double maxLng, @Param("maxLat") Double maxLat,
                                              @Param("limit") Integer limit);
}
