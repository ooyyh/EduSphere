package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.controller.LearningController.CourseProgressResponse;
import top.ooyyh.edusphere.entity.LearningProgress;

import java.util.List;

public interface LearningService {
    
    /**
     * 记录学习进度
     */
    void recordProgress(LearningProgress progress);
    
    /**
     * 获取课程学习进度
     */
    CourseProgressResponse getCourseProgress(Integer userId, Integer courseId);
    
    /**
     * 标记课时完成
     */
    void completeLesson(Integer userId, Integer courseId, Integer lessonId);
    
    /**
     * 获取用户学习记录
     */
    List<LearningProgress> getUserLearningRecords(Integer userId);
}
