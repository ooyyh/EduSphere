package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.CourseSection;
import top.ooyyh.edusphere.entity.CourseLesson;
import top.ooyyh.edusphere.request.CourseCreateRequest;
import top.ooyyh.edusphere.request.SectionCreateRequest;
import top.ooyyh.edusphere.request.LessonCreateRequest;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface TeacherService {
    // 课程管理
    Result<List<Course>> getTeacherCourses(Integer teacherId);
    Result<Course> createCourse(CourseCreateRequest request, Integer teacherId);
    Result<Course> updateCourse(Integer courseId, CourseCreateRequest request, Integer teacherId);
    Result<String> deleteCourse(Integer courseId, Integer teacherId);
    Result<String> publishCourse(Integer courseId, Integer teacherId);
    Result<String> unpublishCourse(Integer courseId, Integer teacherId);
    
    // 章节管理
    Result<List<CourseSection>> getCourseSections(Integer courseId, Integer teacherId);
    Result<CourseSection> createSection(SectionCreateRequest request, Integer teacherId);
    Result<CourseSection> updateSection(Integer sectionId, SectionCreateRequest request, Integer teacherId);
    Result<String> deleteSection(Integer sectionId, Integer teacherId);
    
    // 课时管理
    Result<List<CourseLesson>> getSectionLessons(Integer sectionId, Integer teacherId);
    Result<CourseLesson> createLesson(LessonCreateRequest request, Integer teacherId);
    Result<CourseLesson> updateLesson(Integer lessonId, LessonCreateRequest request, Integer teacherId);
    Result<String> deleteLesson(Integer lessonId, Integer teacherId);
    
    // 统计数据
    Result<Object> getCourseStats(Integer courseId, Integer teacherId);
}
