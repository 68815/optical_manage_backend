package com.optical.manage.mapper;

import com.optical.manage.DO.CableRepeater;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface CableRepeaterMapper extends BaseMapper<CableRepeater> {

    @Select("SELECT repeater_id, name, start_point_id, end_point_id, repeater_point_id, " +
            "length, loss_budget, status, props, created_at, updated_at " +
            "FROM cable_repeater " +
            "WHERE repeater_id = #{id}")
    CableRepeater getById(Long id);

    @Insert("INSERT INTO cable_repeater (name, start_point_id, end_point_id, repeater_point_id, " +
            "length, loss_budget, status, props) " +
            "VALUES (#{name}, #{startPointId}, #{endPointId}, #{repeaterPointId}, " +
            "#{length}, #{lossBudget}, #{status}, #{props})")
    @Options(useGeneratedKeys = true, keyProperty = "repeaterId")
    Long insertRepeater(CableRepeater repeater);

    @Update("<script>" +
            "UPDATE cable_repeater " +
            "SET name = #{name}, " +
            "start_point_id = #{startPointId}, " +
            "end_point_id = #{endPointId}, " +
            "repeater_point_id = #{repeaterPointId}, " +
            "length = #{length}, " +
            "loss_budget = #{lossBudget}, " +
            "status = #{status}, " +
            "props = #{props}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE repeater_id = #{repeaterId}" +
            "</script>")
    int updateRepeater(CableRepeater repeater);

    @Delete("DELETE FROM cable_repeater WHERE repeater_id = #{id}")
    int deleteById(Long id);

    @Select("SELECT repeater_id, name, start_point_id, end_point_id, repeater_point_id, " +
            "length, loss_budget, status, props " +
            "FROM cable_repeater")
    List<CableRepeater> selectAll();
}
