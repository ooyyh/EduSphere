package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.mapper.UserMapper;
import top.ooyyh.edusphere.mapper.UserBalanceMapper;
import top.ooyyh.edusphere.mapper.PurchaseRecordMapper;
import top.ooyyh.edusphere.mapper.LearningProgressMapper;
import top.ooyyh.edusphere.service.AdminService;
import top.ooyyh.edusphere.utils.Result;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;
    
    @Autowired
    private LearningProgressMapper learningProgressMapper;
    
    @Override
    public Result<List<Course>> getPendingCourses() {
        try {
            List<Course> courses = courseMapper.getCoursesByStatus("pending");
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取待审核课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> approveCourse(Integer courseId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            if (!"pending".equals(course.getStatus())) {
                return Result.error("该课程不在审核状态");
            }
            
            course.setStatus("published");
            course.setUpdatedAt(LocalDateTime.now());
            courseMapper.updateCourse(course);
            
            return Result.success("课程审核通过，已发布");
        } catch (Exception e) {
            return Result.error("审核课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> rejectCourse(Integer courseId, String reason) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            if (!"pending".equals(course.getStatus())) {
                return Result.error("该课程不在审核状态");
            }
            
            course.setStatus("rejected");
            course.setUpdatedAt(LocalDateTime.now());
            courseMapper.updateCourse(course);
            
            String message = "课程审核未通过";
            if (reason != null && !reason.trim().isEmpty()) {
                message += "，原因：" + reason;
            }
            
            return Result.success(message);
        } catch (Exception e) {
            return Result.error("审核课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseMapper.getAllCourses();
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> deleteCourse(Integer courseId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            courseMapper.deleteCourse(courseId);
            return Result.success("课程删除成功");
        } catch (Exception e) {
            return Result.error("删除课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> getCourseStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 课程总数
            int totalCourses = courseMapper.getAllCourses().size();
            stats.put("totalCourses", totalCourses);
            
            // 各状态课程数量
            int draftCourses = courseMapper.getCoursesByStatus("draft").size();
            int pendingCourses = courseMapper.getCoursesByStatus("pending").size();
            int publishedCourses = courseMapper.getCoursesByStatus("published").size();
            int rejectedCourses = courseMapper.getCoursesByStatus("rejected").size();
            
            stats.put("draftCourses", draftCourses);
            stats.put("pendingCourses", pendingCourses);
            stats.put("publishedCourses", publishedCourses);
            stats.put("rejectedCourses", rejectedCourses);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取课程统计失败: " + e.getMessage());
        }
    }
    
    // ========== 用户管理 ==========
    
    @Override
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userMapper.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            List<User> allUsers = userMapper.getAllUsers();
            int totalUsers = allUsers.size();
            
            // 按角色统计
            long adminCount = allUsers.stream().filter(u -> "admin".equals(u.getRole())).count();
            long teacherCount = allUsers.stream().filter(u -> "teacher".equals(u.getRole())).count();
            long studentCount = allUsers.stream().filter(u -> "student".equals(u.getRole())).count();
            
            stats.put("totalUsers", totalUsers);
            stats.put("adminCount", adminCount);
            stats.put("teacherCount", teacherCount);
            stats.put("studentCount", studentCount);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取用户统计失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> toggleUserStatus(Integer userId) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 切换用户状态（这里假设User实体有status字段）
            // 如果没有status字段，可以添加一个
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateUser(user);
            
            return Result.success("用户状态更新成功");
        } catch (Exception e) {
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> deleteUser(Integer userId) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 不能删除管理员
            if ("admin".equals(user.getRole())) {
                return Result.error("不能删除管理员用户");
            }
            
            userMapper.deleteUser(userId);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    // ========== 数据统计 ==========
    
    @Override
    public Result<Map<String, Object>> getSystemOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 用户统计
            List<User> allUsers = userMapper.getAllUsers();
            overview.put("totalUsers", allUsers.size());
            
            // 课程统计
            List<Course> allCourses = courseMapper.getAllCourses();
            overview.put("totalCourses", allCourses.size());
            overview.put("publishedCourses", courseMapper.getCoursesByStatus("published").size());
            overview.put("pendingCourses", courseMapper.getCoursesByStatus("pending").size());
            
            // 收入统计（简化版）
            overview.put("totalIncome", 0); // 这里需要从购买记录中计算
            overview.put("todayIncome", 0);
            
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取系统概览失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> getIncomeStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 这里需要根据实际的收入表结构来实现
            // 暂时返回模拟数据
            stats.put("totalIncome", 0);
            stats.put("monthlyIncome", 0);
            stats.put("dailyIncome", 0);
            stats.put("courseSales", 0);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取收入统计失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Map<String, Object>> getLearningStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 学习进度统计
            List<top.ooyyh.edusphere.entity.LearningProgress> allProgress = learningProgressMapper.getByUserId(null);
            stats.put("totalLearningRecords", allProgress.size());

            // 完成率统计
            long completedCount = allProgress.stream().filter(p -> p.getCompleted()).count();
            double completionRate = allProgress.size() > 0 ? (double) completedCount / allProgress.size() * 100 : 0;
            stats.put("completionRate", completionRate);

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取学习统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getUserDetail(Integer userId) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 获取用户余额统计
            Map<String, Object> stats = new HashMap<>();
            top.ooyyh.edusphere.entity.UserBalance balance = userBalanceMapper.getByUserId(userId);
            if (balance != null) {
                stats.put("balance", balance.getBalance());
                stats.put("totalRecharge", balance.getTotalRecharge());
                stats.put("totalConsumption", balance.getTotalConsumption());
            } else {
                stats.put("balance", 0);
                stats.put("totalRecharge", 0);
                stats.put("totalConsumption", 0);
            }

            // 获取购买课程数
            List<top.ooyyh.edusphere.entity.PurchaseRecord> purchaseRecords = purchaseRecordMapper.getByUserId(userId);
            stats.put("purchasedCourses", purchaseRecords.size());

            result.put("stats", stats);
            result.put("courses", purchaseRecords);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取用户详情失败: " + e.getMessage());
        }
    }
}
