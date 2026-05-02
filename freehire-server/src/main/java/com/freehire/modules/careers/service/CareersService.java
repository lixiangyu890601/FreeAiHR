package com.freehire.modules.careers.service;

import com.freehire.modules.careers.dto.ApplyDTO;
import com.freehire.modules.careers.dto.CompanyInfoVO;
import com.freehire.modules.careers.dto.JobVO;

import java.util.List;

/**
 * 公开招聘服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface CareersService {

    /**
     * 获取公司信息
     */
    CompanyInfoVO getCompanyInfo();

    /**
     * 获取在招职位列表
     *
     * @param keyword  搜索关键词
     * @param city     城市
     * @param jobType  职位类型
     */
    List<JobVO> getJobList(String keyword, String city, String jobType);

    /**
     * 获取职位详情
     */
    JobVO getJobDetail(Long jobId);

    /**
     * 投递简历
     *
     * @return 申请ID
     */
    Long apply(ApplyDTO dto);

    /**
     * 检查是否已投递
     */
    boolean hasApplied(Long jobId, String phone);

    /**
     * 获取城市列表（有在招职位的城市）
     */
    List<String> getCityOptions();
}

