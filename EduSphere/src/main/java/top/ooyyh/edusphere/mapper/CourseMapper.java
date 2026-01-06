package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.request.CourseSearchRequest;

import java.util.List;

@Mapper
public interface CourseMapper {
    // 根据ID查询课程详情
    Course getCourseById(Integer id);
    
    // 分页查询课程列表
    List<Course> getCourseList(CourseSearchRequest request);
    
    // 查询课程总数
    Integer getCourseCount(CourseSearchRequest request);
    
    // 查询热门课程
    List<Course> getHotCourses(@Param("limit") Integer limit);
    
    // 查询最新课程
    List<Course> getNewCourses(@Param("limit") Integer limit);
    
    // 根据分类查询课程
    List<Course> getCoursesByCategory(@Param("categoryId") Integer categoryId, @Param("limit") Integer limit);
    
    // 更新课程评分
    void updateCourseRating(@Param("courseId") Integer courseId);
    
    // 更新课程学员数
    void updateStudentCount(@Param("courseId") Integer courseId);
    
    // 根据讲师ID查询课程
    List<Course> getCoursesByInstructor(@Param("instructorId") Integer instructorId);
    
    // 插入课程
    void insertCourse(Course course);
    
    // 更新课程
    void updateCourse(Course course);
    
    // 删除课程
    void deleteCourse(@Param("id") Integer id);
    
    // 根据状态查询课程
    List<Course> getCoursesByStatus(@Param("status") String status);
    
    // 获取所有课程（管理员）
    List<Course> getAllCourses();
    
    // 添加用户课程关系
    void insertUserCourse(@Param("userId") Integer userId, 
                         @Param("courseId") Integer courseId, 
                         @Param("purchasePrice") java.math.BigDecimal purchasePrice);
}
