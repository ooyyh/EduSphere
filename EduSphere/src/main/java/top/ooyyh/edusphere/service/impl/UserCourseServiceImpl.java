package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.UserCourse;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.mapper.UserCourseMapper;
import top.ooyyh.edusphere.service.UserCourseService;
import top.ooyyh.edusphere.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserCourseServiceImpl implements UserCourseService {
    @Autowired
    private UserCourseMapper userCourseMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public Result<List<Course>> getUserCourses(Integer userId) {
        try {
            List<UserCourse> userCourses = userCourseMapper.getUserCourses(userId);
            // 根据userCourses获取课程详情
            List<Course> courses = new ArrayList<>();
            for (UserCourse userCourse : userCourses) {
                Course course = courseMapper.getCourseById(userCourse.getCourseId());
                if (course != null) {
                    courses.add(course);
                }
            }
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取用户课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> hasUserPurchased(Integer userId, Integer courseId) {
        try {
            boolean hasPurchased = userCourseMapper.hasUserPurchased(userId, courseId);
            return Result.success(hasPurchased);
        } catch (Exception e) {
            return Result.error("检查购买状态失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> purchaseCourse(Integer userId, Integer courseId) {
        try {
            // 检查是否已购买
            if (userCourseMapper.hasUserPurchased(userId, courseId)) {
                return Result.error("您已经购买过该课程");
            }
            
            // 获取课程信息
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 创建用户课程关系
            UserCourse userCourse = new UserCourse();
            userCourse.setUserId(userId);
            userCourse.setCourseId(courseId);
            userCourse.setPurchasePrice(course.getPrice());
            userCourse.setPurchaseTime(LocalDateTime.now());
            userCourse.setStatus(1);
            userCourse.setCreatedAt(LocalDateTime.now());
            
            int result = userCourseMapper.addUserCourse(userCourse);
            if (result > 0) {
                return Result.success("购买成功");
            } else {
                return Result.error("购买失败");
            }
        } catch (Exception e) {
            return Result.error("购买课程失败: " + e.getMessage());
        }
    }
}
