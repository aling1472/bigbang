package com.tencent.wxcloudrun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tencent.wxcloudrun.model.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
   // @Select("select * from device where city = #{city} and department = #{department}")
   // List<Device> findByCityAndDepartment(@Param("city")String city, @Param("department")Integer department);

}
