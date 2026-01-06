package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.LearningProgress;

import java.util.List;

@Mapper
public interface LearningProgressMapper {
    
    /**
     * 根据用户ID和课时ID获取学习进度
     */
    LearningProgress getByUserAndLesson(@Param("userId") Integer userId, 
                                        @Param("lessonId") Integer lessonId);
    
    /**
     * 插入学习进度记录
     */
    void insertProgress(LearningProgress progress);
    
    /**
     * 更新学习进度记录
     */
    void updateProgress(LearningProgress progress);
    
    /**
     * 根据用户ID获取学习记录
     */
    List<LearningProgress> getByUserId(@Param("userId") Integer userId);
    
    /**
     * 获取用户在指定课程中已完成的课时数
     */
    int getCompletedLessonsByUserAndCourse(@Param("userId") Integer userId, 
                                          @Param("courseId") Integer courseId);
}
