package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.controller.LearningController.CourseProgressResponse;
import top.ooyyh.edusphere.entity.CourseLesson;
import top.ooyyh.edusphere.entity.LearningProgress;
import top.ooyyh.edusphere.mapper.CourseLessonMapper;
import top.ooyyh.edusphere.mapper.CourseSectionMapper;
import top.ooyyh.edusphere.mapper.LearningProgressMapper;
import top.ooyyh.edusphere.service.LearningService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearningServiceImpl implements LearningService {
    
    @Autowired
    private LearningProgressMapper learningProgressMapper;
    
    @Autowired
    private CourseSectionMapper courseSectionMapper;
    
    @Autowired
    private CourseLessonMapper courseLessonMapper;
    
    @Override
    public void recordProgress(LearningProgress progress) {
        try {
            // 检查是否已存在记录
            LearningProgress existingProgress = learningProgressMapper.getByUserAndLesson(
                progress.getUserId(), progress.getLessonId()
            );
            
            if (existingProgress != null) {
                // 更新现有记录
                existingProgress.setProgress(progress.getProgress());
                existingProgress.setCompleted(progress.getCompleted());
                existingProgress.setUpdatedAt(LocalDateTime.now());
                learningProgressMapper.updateProgress(existingProgress);
            } else {
                // 创建新记录
                progress.setCreatedAt(LocalDateTime.now());
                progress.setUpdatedAt(LocalDateTime.now());
                learningProgressMapper.insertProgress(progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("记录学习进度失败: " + e.getMessage());
        }
    }
    
    @Override
    public CourseProgressResponse getCourseProgress(Integer userId, Integer courseId) {
        try {
            // 获取课程总课时数
            List<Integer> sectionIds = courseSectionMapper.getSectionIdsByCourseId(courseId);
            int totalLessons = 0;
            for (Integer sectionId : sectionIds) {
                totalLessons += courseLessonMapper.getLessonCountBySectionId(sectionId);
            }
            
            // 获取已完成课时数
            int completedLessons = learningProgressMapper.getCompletedLessonsByUserAndCourse(userId, courseId);
            
            return new CourseProgressResponse(courseId, totalLessons, completedLessons);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取学习进度失败: " + e.getMessage());
        }
    }
    
    @Override
    public void completeLesson(Integer userId, Integer courseId, Integer lessonId) {
        try {
            // 检查课时是否存在
            CourseLesson lesson = courseLessonMapper.getLessonById(lessonId);
            if (lesson == null) {
                throw new RuntimeException("课时不存在");
            }
            
            // 记录完成状态
            LearningProgress progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setCourseId(courseId);
            progress.setLessonId(lessonId);
            progress.setProgress(100);
            progress.setCompleted(true);
            
            recordProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("标记课时完成失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<LearningProgress> getUserLearningRecords(Integer userId) {
        try {
            return learningProgressMapper.getByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取学习记录失败: " + e.getMessage());
        }
    }
}
