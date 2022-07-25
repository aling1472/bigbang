package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceMapper {
    @Select("select * from device where city = #{city} and department = #{department}")
    List<Device> findByCityAndDepartment(@Param("city")String city, @Param("department")Integer department);
}
