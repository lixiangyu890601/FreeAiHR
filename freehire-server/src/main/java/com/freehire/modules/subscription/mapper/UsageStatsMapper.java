package com.freehire.modules.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.subscription.entity.UsageStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用量统计Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface UsageStatsMapper extends BaseMapper<UsageStats> {

    /**
     * 增加职位计数
     */
    @Update("UPDATE usage_stats SET job_count = job_count + #{delta} WHERE stat_month = #{statMonth}")
    int incrementJobCount(@Param("statMonth") String statMonth, @Param("delta") int delta);

    /**
     * 增加简历计数
     */
    @Update("UPDATE usage_stats SET resume_count = resume_count + #{delta} WHERE stat_month = #{statMonth}")
    int incrementResumeCount(@Param("statMonth") String statMonth, @Param("delta") int delta);

    /**
     * 增加候选人计数
     */
    @Update("UPDATE usage_stats SET candidate_count = candidate_count + #{delta} WHERE stat_month = #{statMonth}")
    int incrementCandidateCount(@Param("statMonth") String statMonth, @Param("delta") int delta);

    /**
     * 增加AI解析计数
     */
    @Update("UPDATE usage_stats SET ai_parse_count = ai_parse_count + #{delta} WHERE stat_month = #{statMonth}")
    int incrementAiParseCount(@Param("statMonth") String statMonth, @Param("delta") int delta);

    /**
     * 增加AI匹配计数
     */
    @Update("UPDATE usage_stats SET ai_match_count = ai_match_count + #{delta} WHERE stat_month = #{statMonth}")
    int incrementAiMatchCount(@Param("statMonth") String statMonth, @Param("delta") int delta);
}

