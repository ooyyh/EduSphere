package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;
import java.util.Map;

public interface AdminService {
    
    // ========== 课程管理 ==========
    
    /**
     * 获取待审核课程列表
     */
    Result<List<Course>> getPendingCourses();
    
    /**
     * 审核通过课程
     */
    Result<String> approveCourse(Integer courseId);
    
    /**
     * 审核拒绝课程
     */
    Result<String> rejectCourse(Integer courseId, String reason);
    
    /**
     * 获取所有课程（管理员视图）
     */
    Result<List<Course>> getAllCourses();
    
    /**
     * 删除课程
     */
    Result<String> deleteCourse(Integer courseId);
    
    /**
     * 获取课程统计信息
     */
    Result<Map<String, Object>> getCourseStats();
    
    // ========== 用户管理 ==========
    
    /**
     * 获取所有用户列表
     */
    Result<List<User>> getAllUsers();
    
    /**
     * 获取用户统计信息
     */
    Result<Map<String, Object>> getUserStats();
    
    /**
     * 禁用/启用用户
     */
    Result<String> toggleUserStatus(Integer userId);
    
    /**
     * 删除用户
     */
    Result<String> deleteUser(Integer userId);
    
    // ========== 数据统计 ==========
    
    /**
     * 获取系统概览数据
     */
    Result<Map<String, Object>> getSystemOverview();
    
    /**
     * 获取收入统计
     */
    Result<Map<String, Object>> getIncomeStats();
    
    /**
     * 获取学习统计
     */
    Result<Map<String, Object>> getLearningStats();

    /**
     * 获取用户详细信息
     */
    Result<Map<String, Object>> getUserDetail(Integer userId);
}
