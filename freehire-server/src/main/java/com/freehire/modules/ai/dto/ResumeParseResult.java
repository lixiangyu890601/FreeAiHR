package com.freehire.modules.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * 简历解析结果
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class ResumeParseResult {

    /**
     * 基本信息
     */
    private BasicInfo basicInfo;

    /**
     * 教育经历
     */
    private List<Education> educations;

    /**
     * 工作经历
     */
    private List<WorkExperience> workExperiences;

    /**
     * 项目经历
     */
    private List<Project> projects;

    /**
     * 技能列表
     */
    private List<String> skills;

    /**
     * 证书列表
     */
    private List<String> certificates;

    /**
     * 自我评价
     */
    private String selfEvaluation;

    /**
     * 摘要/总结
     */
    private String summary;

    // ========== 快捷方法（直接访问BasicInfo中的字段） ==========

    public String getName() {
        return basicInfo != null ? basicInfo.getName() : null;
    }

    public void setName(String name) {
        ensureBasicInfo().setName(name);
    }

    public String getPhone() {
        return basicInfo != null ? basicInfo.getPhone() : null;
    }

    public void setPhone(String phone) {
        ensureBasicInfo().setPhone(phone);
    }

    public String getEmail() {
        return basicInfo != null ? basicInfo.getEmail() : null;
    }

    public void setEmail(String email) {
        ensureBasicInfo().setEmail(email);
    }

    public String getGender() {
        return basicInfo != null ? basicInfo.getGender() : null;
    }

    public void setGender(String gender) {
        ensureBasicInfo().setGender(gender);
    }

    public Integer getAge() {
        return basicInfo != null ? basicInfo.getAge() : null;
    }

    public void setAge(Integer age) {
        ensureBasicInfo().setAge(age);
    }

    public String getCity() {
        return basicInfo != null ? basicInfo.getCity() : null;
    }

    public void setCity(String city) {
        ensureBasicInfo().setCity(city);
    }

    public String getEducation() {
        return basicInfo != null ? basicInfo.getEducation() : null;
    }

    public void setEducation(String education) {
        ensureBasicInfo().setEducation(education);
    }

    public String getSchool() {
        // 优先从 BasicInfo 获取（AI解析结果），然后从 educations 获取
        if (basicInfo != null && basicInfo.getSchool() != null) {
            return basicInfo.getSchool();
        }
        return educations != null && !educations.isEmpty() ? educations.get(0).getSchool() : null;
    }

    public void setSchool(String school) {
        ensureBasicInfo().setSchool(school);
    }

    public String getMajor() {
        // 优先从 BasicInfo 获取（AI解析结果），然后从 educations 获取
        if (basicInfo != null && basicInfo.getMajor() != null) {
            return basicInfo.getMajor();
        }
        return educations != null && !educations.isEmpty() ? educations.get(0).getMajor() : null;
    }

    public void setMajor(String major) {
        ensureBasicInfo().setMajor(major);
    }

    public Integer getWorkYears() {
        return basicInfo != null ? basicInfo.getWorkYears() : null;
    }

    public void setWorkYears(Integer workYears) {
        ensureBasicInfo().setWorkYears(workYears);
    }

    public String getCurrentCompany() {
        return basicInfo != null ? basicInfo.getCurrentCompany() : null;
    }

    public void setCurrentCompany(String currentCompany) {
        ensureBasicInfo().setCurrentCompany(currentCompany);
    }

    public String getCurrentPosition() {
        return basicInfo != null ? basicInfo.getCurrentPosition() : null;
    }

    public void setCurrentPosition(String currentPosition) {
        ensureBasicInfo().setCurrentPosition(currentPosition);
    }

    public String getExpectPosition() {
        return basicInfo != null ? basicInfo.getExpectPosition() : null;
    }

    public void setExpectPosition(String expectPosition) {
        ensureBasicInfo().setExpectPosition(expectPosition);
    }

    public String getExpectCity() {
        return basicInfo != null ? basicInfo.getExpectCity() : null;
    }

    public void setExpectCity(String expectCity) {
        ensureBasicInfo().setExpectCity(expectCity);
    }

    public void setExpectSalary(String expectSalary) {
        // 尝试解析为数字
        if (expectSalary != null && basicInfo != null) {
            try {
                String numStr = expectSalary.replaceAll("[^0-9]", "");
                if (!numStr.isEmpty()) {
                    ensureBasicInfo().setExpectSalaryMin(Integer.parseInt(numStr));
                }
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
    }

    private BasicInfo ensureBasicInfo() {
        if (basicInfo == null) {
            basicInfo = new BasicInfo();
        }
        return basicInfo;
    }

    @Data
    public static class BasicInfo {
        private String name;
        private String phone;
        private String email;
        private String gender;
        private String birthDate;
        private Integer age;
        private String city;
        private String education;
        private String school;      // 毕业院校
        private String major;       // 专业
        private Integer workYears;
        private String currentCompany;
        private String currentPosition;
        private String expectPosition;
        private String expectCity;
        private Integer expectSalaryMin;
        private Integer expectSalaryMax;
    }

    @Data
    public static class Education {
        private String school;
        private String major;
        private String degree;
        private String startDate;
        private String endDate;
        private String description;
    }

    @Data
    public static class WorkExperience {
        private String company;
        private String position;
        private String department;
        private String startDate;
        private String endDate;
        private String description;
        private List<String> achievements;
    }

    @Data
    public static class Project {
        private String name;
        private String role;
        private String startDate;
        private String endDate;
        private String description;
        private List<String> technologies;
        private List<String> achievements;
    }
}

