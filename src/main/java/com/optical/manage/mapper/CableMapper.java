package com.optical.manage.mapper;

import com.optical.manage.DO.Cable;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface CableMapper extends BaseMapper<Cable> {

    @Select("SELECT cable_id, name, cable_level, start_point_id, end_point_id, total_length, " +
            "total_fiber_count, operator, status, props, created_at, updated_at " +
            "FROM cable " +
            "WHERE cable_id = #{id}")
    Cable getById(Long id);

    @Insert("INSERT INTO cable (name, cable_level, start_point_id, end_point_id, total_length, " +
            "total_fiber_count, operator, status, props) " +
            "VALUES (#{name}, #{cableLevel}, #{startPointId}, #{endPointId}, #{totalLength}, " +
            "#{totalFiberCount}, #{operator}, #{status}, #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "cableId")
    Long insertCable(Cable cable);

    @Update("<script>" +
            "UPDATE cable " +
            "SET name = #{name}, " +
            "cable_level = #{cableLevel}, " +
            "start_point_id = #{startPointId}, " +
            "end_point_id = #{endPointId}, " +
            "total_length = #{totalLength}, " +
            "total_fiber_count = #{totalFiberCount}, " +
            "operator = #{operator}, " +
            "status = #{status}, " +
            "props = #{props}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE cable_id = #{cableId}" +
            "</script>")
    int updateCable(Cable cable);

    @Delete("DELETE FROM cable WHERE cable_id = #{id}")
    int deleteById(Long id);

    @Select("SELECT cable_id, name, cable_level, start_point_id, end_point_id, total_length, " +
            "total_fiber_count, operator, status, props " +
            "FROM cable")
    List<Cable> selectAll();
}
