package com.freehire.modules.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.subscription.entity.SubscriptionPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐配置Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface SubscriptionPlanMapper extends BaseMapper<SubscriptionPlan> {
}

