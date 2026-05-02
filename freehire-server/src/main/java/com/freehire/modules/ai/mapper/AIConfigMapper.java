package com.freehire.modules.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.ai.entity.AIConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * AI配置Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface AIConfigMapper extends BaseMapper<AIConfig> {

    /**
     * 获取默认配置
     */
    @Select("SELECT * FROM ai_config WHERE is_default = 1 AND status = 1 LIMIT 1")
    AIConfig selectDefault();
}
