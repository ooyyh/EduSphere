package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.service.AdminService;
import top.ooyyh.edusphere.utils.Result;
import top.ooyyh.edusphere.config.RequireRole;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 获取待审核课程列表
     */
    @GetMapping("/courses/pending")
    @RequireRole({"admin"})
    public Result<List<Course>> getPendingCourses(HttpServletRequest request) {
        return adminService.getPendingCourses();
    }
    
    /**
     * 审核通过课程
     */
    @PostMapping("/courses/{courseId}/approve")
    @RequireRole({"admin"})
    public Result<String> approveCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        return adminService.approveCourse(courseId);
    }
    
    /**
     * 审核拒绝课程
     */
    @PostMapping("/courses/{courseId}/reject")
    @RequireRole({"admin"})
    public Result<String> rejectCourse(@PathVariable Integer courseId, @RequestParam(required = false) String reason, HttpServletRequest request) {
        return adminService.rejectCourse(courseId, reason);
    }
    
    /**
     * 获取所有课程（管理员视图）
     */
    @GetMapping("/courses")
    @RequireRole({"admin"})
    public Result<List<Course>> getAllCourses(HttpServletRequest request) {
        return adminService.getAllCourses();
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/courses/{courseId}")
    @RequireRole({"admin"})
    public Result<String> deleteCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        return adminService.deleteCourse(courseId);
    }
    
    /**
     * 获取课程统计信息
     */
    @GetMapping("/courses/stats")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> getCourseStats(HttpServletRequest request) {
        return adminService.getCourseStats();
    }
    
    // ========== 用户管理 ==========
    
    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    @RequireRole({"admin"})
    public Result<List<User>> getAllUsers(HttpServletRequest request) {
        return adminService.getAllUsers();
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/users/stats")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        return adminService.getUserStats();
    }
    
    /**
     * 禁用/启用用户
     */
    @PutMapping("/users/{userId}/toggle")
    @RequireRole({"admin"})
    public Result<String> toggleUserStatus(@PathVariable Integer userId, HttpServletRequest request) {
        return adminService.toggleUserStatus(userId);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    @RequireRole({"admin"})
    public Result<String> deleteUser(@PathVariable Integer userId, HttpServletRequest request) {
        return adminService.deleteUser(userId);
    }
    
    // ========== 数据统计 ==========
    
    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> getSystemOverview(HttpServletRequest request) {
        return adminService.getSystemOverview();
    }
    
    /**
     * 获取收入统计
     */
    @GetMapping("/income/stats")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> getIncomeStats(HttpServletRequest request) {
        return adminService.getIncomeStats();
    }
    
    /**
     * 获取学习统计
     */
    @GetMapping("/learning/stats")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> getLearningStats(HttpServletRequest request) {
        return adminService.getLearningStats();
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping("/users/{userId}/detail")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> getUserDetail(@PathVariable Integer userId, HttpServletRequest request) {
        return adminService.getUserDetail(userId);
    }
}
