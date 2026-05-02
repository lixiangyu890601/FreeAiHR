package com.freehire.modules.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.resume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 简历Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

    /**
     * 统计简历数量
     */
    @Select("SELECT COUNT(*) FROM resume WHERE deleted = 0")
    int countResumes();

    /**
     * 统计指定时间范围内的简历数量
     */
    @Select("""
            SELECT COUNT(*) FROM resume 
            WHERE create_time >= #{startTime} AND create_time < #{endTime} 
            AND deleted = 0
            """)
    long countByTimeRange(@Param("startTime") LocalDateTime startTime, 
                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各来源的简历数量（返回中文名称）
     */
    @Select("""
            SELECT 
                CASE source
                    WHEN 'upload' THEN 'HR上传'
                    WHEN 'website' THEN '官网投递'
                    WHEN 'email' THEN '邮件投递'
                    WHEN 'referral' THEN '内推'
                    ELSE '其他'
                END as source,
                COUNT(*) as count 
            FROM resume 
            WHERE deleted = 0 
            GROUP BY source
            ORDER BY count DESC
            """)
    java.util.List<java.util.Map<String, Object>> countBySource();
}

