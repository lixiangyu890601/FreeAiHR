package com.freehire.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.system.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 根据键获取配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{key}")
    String getValueByKey(@Param("key") String key);

    /**
     * 根据类型获取配置列表
     */
    @Select("SELECT * FROM sys_config WHERE config_type = #{type} ORDER BY id")
    List<Config> selectByType(@Param("type") String type);

    /**
     * 根据键获取配置
     */
    @Select("SELECT * FROM sys_config WHERE config_key = #{key}")
    Config selectByKey(@Param("key") String key);
}

