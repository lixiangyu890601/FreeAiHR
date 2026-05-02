package com.freehire.modules.interview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.interview.entity.Interview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface InterviewMapper extends BaseMapper<Interview> {

    /**
     * 查询某时间段的面试
     */
    @Select("""
            SELECT * FROM interview 
            WHERE interview_time BETWEEN #{startTime} AND #{endTime} 
            AND status != 'cancelled' AND deleted = 0
            ORDER BY interview_time
            """)
    List<Interview> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查询面试官的面试安排
     */
    @Select("""
            SELECT * FROM interview 
            WHERE interviewer_ids LIKE CONCAT('%', #{interviewerId}, '%')
            AND status != 'cancelled' AND deleted = 0
            ORDER BY interview_time DESC
            """)
    List<Interview> selectByInterviewerId(@Param("interviewerId") Long interviewerId);

    /**
     * 统计今日面试数量
     */
    @Select("""
            SELECT COUNT(*) FROM interview 
            WHERE DATE(interview_time) = CURRENT_DATE 
            AND status != 'cancelled' AND deleted = 0
            """)
    int countTodayInterviews();
}

