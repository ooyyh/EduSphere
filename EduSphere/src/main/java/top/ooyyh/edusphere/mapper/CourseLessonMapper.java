package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.CourseLesson;

import java.util.List;

@Mapper
public interface CourseLessonMapper {
    // 根据章节ID查询课时
    List<CourseLesson> getLessonsBySectionId(@Param("sectionId") Integer sectionId);
    
    // 根据ID查询课时
    CourseLesson getLessonById(@Param("id") Integer id);
    
    // 插入课时
    void insertLesson(CourseLesson lesson);
    
    // 更新课时
    void updateLesson(CourseLesson lesson);
    
    // 删除课时
    void deleteLesson(@Param("id") Integer id);
    
    // 根据章节ID获取课时数量
    int getLessonCountBySectionId(@Param("sectionId") Integer sectionId);
}