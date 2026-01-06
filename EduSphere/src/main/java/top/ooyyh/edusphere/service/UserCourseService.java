package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.UserCourse;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface UserCourseService {
    // 获取用户购买的课程
    Result<List<Course>> getUserCourses(Integer userId);
    
    // 检查用户是否已购买课程
    Result<Boolean> hasUserPurchased(Integer userId, Integer courseId);
    
    // 购买课程
    Result<String> purchaseCourse(Integer userId, Integer courseId);
}
