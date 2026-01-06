package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.CourseSection;

import java.util.List;

@Mapper
public interface CourseSectionMapper {
    // 根据课程ID查询章节
    List<CourseSection> getSectionsByCourseId(@Param("courseId") Integer courseId);
    
    // 根据ID查询章节
    CourseSection getSectionById(@Param("id") Integer id);
    
    // 插入章节
    void insertSection(CourseSection section);
    
    // 更新章节
    void updateSection(CourseSection section);
    
    // 删除章节
    void deleteSection(@Param("id") Integer id);
    
    // 根据课程ID获取章节ID列表
    List<Integer> getSectionIdsByCourseId(@Param("courseId") Integer courseId);
}