package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.CourseSection;
import top.ooyyh.edusphere.entity.CourseLesson;
import top.ooyyh.edusphere.request.CourseCreateRequest;
import top.ooyyh.edusphere.request.SectionCreateRequest;
import top.ooyyh.edusphere.request.LessonCreateRequest;
import top.ooyyh.edusphere.service.TeacherService;
import top.ooyyh.edusphere.utils.Result;
import top.ooyyh.edusphere.config.RequireRole;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/teacher")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * 获取当前用户ID（从JWT中解析）
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        // 从JWT拦截器设置的属性中获取用户ID
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return (Integer) userIdObj;
        }
        // 如果获取失败，返回-1表示未登录
        return -1;
    }
    
    /**
     * 获取教师的课程列表
     */
    @GetMapping("/courses")
    @RequireRole({"teacher", "admin"})
    public Result<List<Course>> getTeacherCourses(HttpServletRequest request) {
        // 从JWT中获取当前用户ID，而不是从参数中获取
        Integer teacherId = getCurrentUserId(request);
        return teacherService.getTeacherCourses(teacherId);
    }
    
    /**
     * 创建课程
     */
    @PostMapping("/courses")
    @RequireRole({"teacher", "admin"})
    public Result<Course> createCourse(@RequestBody CourseCreateRequest request, HttpServletRequest httpRequest) {
        return teacherService.createCourse(request, getCurrentUserId(httpRequest));
    }
    
    /**
     * 更新课程
     */
    @PutMapping("/courses/{courseId}")
    @RequireRole({"teacher", "admin"})
    public Result<Course> updateCourse(@PathVariable Integer courseId, @RequestBody CourseCreateRequest request, HttpServletRequest httpRequest) {
        return teacherService.updateCourse(courseId, request, getCurrentUserId(httpRequest));
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/courses/{courseId}")
    @RequireRole({"teacher", "admin"})
    public Result<String> deleteCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        return teacherService.deleteCourse(courseId, getCurrentUserId(request));
    }
    
    /**
     * 发布课程
     */
    @PostMapping("/courses/{courseId}/publish")
    @RequireRole({"teacher", "admin"})
    public Result<String> publishCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        return teacherService.publishCourse(courseId, getCurrentUserId(request));
    }
    
    /**
     * 下架课程
     */
    @PostMapping("/courses/{courseId}/unpublish")
    @RequireRole({"teacher", "admin"})
    public Result<String> unpublishCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        return teacherService.unpublishCourse(courseId, getCurrentUserId(request));
    }
    
    /**
     * 获取课程章节
     */
    @GetMapping("/courses/{courseId}/sections")
    @RequireRole({"teacher", "admin"})
    public Result<List<CourseSection>> getCourseSections(@PathVariable Integer courseId, HttpServletRequest request) {
        return teacherService.getCourseSections(courseId, getCurrentUserId(request));
    }
    
    /**
     * 创建章节
     */
    @PostMapping("/courses/{courseId}/sections")
    @RequireRole({"teacher", "admin"})
    public Result<CourseSection> createSection(@PathVariable Integer courseId, @RequestBody SectionCreateRequest request, HttpServletRequest httpRequest) {
        request.setCourseId(courseId);
        return teacherService.createSection(request, getCurrentUserId(httpRequest));
    }
    
    /**
     * 更新章节
     */
    @PutMapping("/sections/{sectionId}")
    @RequireRole({"teacher", "admin"})
    public Result<CourseSection> updateSection(@PathVariable Integer sectionId, @RequestBody SectionCreateRequest request, HttpServletRequest httpRequest) {
        return teacherService.updateSection(sectionId, request, getCurrentUserId(httpRequest));
    }
    
    /**
     * 删除章节
     */
    @DeleteMapping("/sections/{sectionId}")
    @RequireRole({"teacher", "admin"})
    public Result<String> deleteSection(@PathVariable Integer sectionId, HttpServletRequest request) {
        return teacherService.deleteSection(sectionId, getCurrentUserId(request));
    }
    
    /**
     * 获取章节课时
     */
    @GetMapping("/sections/{sectionId}/lessons")
    @RequireRole({"teacher", "admin"})
    public Result<List<CourseLesson>> getSectionLessons(@PathVariable Integer sectionId, HttpServletRequest request) {
        return teacherService.getSectionLessons(sectionId, getCurrentUserId(request));
    }
    
    /**
     * 创建课时
     */
    @PostMapping("/sections/{sectionId}/lessons")
    @RequireRole({"teacher", "admin"})
    public Result<CourseLesson> createLesson(@PathVariable Integer sectionId, @RequestBody LessonCreateRequest request, HttpServletRequest httpRequest) {
        request.setSectionId(sectionId);
        return teacherService.createLesson(request, getCurrentUserId(httpRequest));
    }
    
    /**
     * 更新课时
     */
    @PutMapping("/lessons/{lessonId}")
    @RequireRole({"teacher", "admin"})
    public Result<CourseLesson> updateLesson(@PathVariable Integer lessonId, @RequestBody LessonCreateRequest request, HttpServletRequest httpRequest) {
        return teacherService.updateLesson(lessonId, request, getCurrentUserId(httpRequest));
    }
    
    /**
     * 删除课时
     */
    @DeleteMapping("/lessons/{lessonId}")
    @RequireRole({"teacher", "admin"})
    public Result<String> deleteLesson(@PathVariable Integer lessonId, HttpServletRequest request) {
        return teacherService.deleteLesson(lessonId, getCurrentUserId(request));
    }
    
    /**
     * 获取课程统计数据
     */
    @GetMapping("/courses/{courseId}/stats")
    @RequireRole({"teacher", "admin"})
    public Result<Object> getCourseStats(@PathVariable Integer courseId, HttpServletRequest request) {
        return teacherService.getCourseStats(courseId, getCurrentUserId(request));
    }
}
