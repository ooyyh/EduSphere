package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.LearningProgress;
import top.ooyyh.edusphere.service.LearningService;
import top.ooyyh.edusphere.utils.ResponseUtils;
import top.ooyyh.edusphere.utils.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/learning")
public class LearningController {
    
    @Autowired
    private LearningService learningService;
    
    /**
     * 记录学习进度
     */
    @PostMapping("/progress")
    public void recordProgress(@RequestBody LearningProgressRequest request,
                               HttpServletRequest httpRequest,
                               HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) httpRequest.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            LearningProgress progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setCourseId(request.getCourseId());
            progress.setLessonId(request.getLessonId());
            progress.setProgress(request.getProgress());
            progress.setCompleted(request.getCompleted());
            
            learningService.recordProgress(progress);
            ResponseUtils.writeSuccessResponse(response, "学习进度记录成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "记录学习进度失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程学习进度
     */
    @GetMapping("/progress/{courseId}")
    public void getCourseProgress(@PathVariable Integer courseId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            CourseProgressResponse progressData = learningService.getCourseProgress(userId, courseId);
            ResponseUtils.writeSuccessResponse(response, progressData);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取学习进度失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记课时完成
     */
    @PostMapping("/complete")
    public void completeLesson(@RequestBody LessonCompleteRequest request,
                               HttpServletRequest httpRequest,
                               HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) httpRequest.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            learningService.completeLesson(userId, request.getCourseId(), request.getLessonId());
            ResponseUtils.writeSuccessResponse(response, "课时完成标记成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "标记课时完成失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户学习记录
     */
    @GetMapping("/records")
    public void getLearningRecords(HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            List<LearningProgress> records = learningService.getUserLearningRecords(userId);
            ResponseUtils.writeSuccessResponse(response, records);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取学习记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 学习进度请求类
     */
    public static class LearningProgressRequest {
        private Integer courseId;
        private Integer lessonId;
        private Integer progress;
        private Boolean completed;
        
        // Getters and Setters
        public Integer getCourseId() { return courseId; }
        public void setCourseId(Integer courseId) { this.courseId = courseId; }
        public Integer getLessonId() { return lessonId; }
        public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }
    
    /**
     * 课时完成请求类
     */
    public static class LessonCompleteRequest {
        private Integer courseId;
        private Integer lessonId;
        
        // Getters and Setters
        public Integer getCourseId() { return courseId; }
        public void setCourseId(Integer courseId) { this.courseId = courseId; }
        public Integer getLessonId() { return lessonId; }
        public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
    }
    
    /**
     * 课程进度响应类
     */
    public static class CourseProgressResponse {
        private Integer courseId;
        private Integer totalLessons;
        private Integer completedLessons;
        private Integer progress;
        
        public CourseProgressResponse(Integer courseId, Integer totalLessons, Integer completedLessons) {
            this.courseId = courseId;
            this.totalLessons = totalLessons;
            this.completedLessons = completedLessons;
            this.progress = totalLessons > 0 ? Math.round((completedLessons * 100.0f) / totalLessons) : 0;
        }
        
        // Getters and Setters
        public Integer getCourseId() { return courseId; }
        public void setCourseId(Integer courseId) { this.courseId = courseId; }
        public Integer getTotalLessons() { return totalLessons; }
        public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }
        public Integer getCompletedLessons() { return completedLessons; }
        public void setCompletedLessons(Integer completedLessons) { this.completedLessons = completedLessons; }
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
    }
}
