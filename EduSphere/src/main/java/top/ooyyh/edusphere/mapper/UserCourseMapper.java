package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.UserCourse;

import java.util.List;

@Mapper
public interface UserCourseMapper {
    // 获取用户购买的课程
    List<UserCourse> getUserCourses(@Param("userId") Integer userId);
    
    // 检查用户是否已购买课程
    boolean hasUserPurchased(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
    
    // 添加用户课程关系
    int addUserCourse(UserCourse userCourse);
}
