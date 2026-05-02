package com.freehire.modules.candidate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.candidate.entity.Candidate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 候选人Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface CandidateMapper extends BaseMapper<Candidate> {

    /**
     * 统计候选人数量
     */
    @Select("SELECT COUNT(*) FROM candidate WHERE deleted = 0")
    int countCandidates();

    /**
     * 根据手机号查找候选人
     */
    @Select("SELECT * FROM candidate WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    Candidate selectByPhone(String phone);

    /**
     * 根据邮箱查找候选人
     */
    @Select("SELECT * FROM candidate WHERE email = #{email} AND deleted = 0 LIMIT 1")
    Candidate selectByEmail(String email);
}

