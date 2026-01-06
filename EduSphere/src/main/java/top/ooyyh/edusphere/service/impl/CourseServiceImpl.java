package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.CourseSection;
import top.ooyyh.edusphere.entity.CourseLesson;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.mapper.CourseSectionMapper;
import top.ooyyh.edusphere.mapper.CourseLessonMapper;
import top.ooyyh.edusphere.mapper.CourseReviewMapper;
import top.ooyyh.edusphere.request.CourseSearchRequest;
import top.ooyyh.edusphere.response.CourseSearchResponse;
import top.ooyyh.edusphere.service.CourseService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseSectionMapper courseSectionMapper;
    
    @Autowired
    private CourseLessonMapper courseLessonMapper;
    
    @Autowired
    private CourseReviewMapper courseReviewMapper;
    
    @Override
    public Result<Course> getCourseDetail(Integer courseId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 获取课程章节和课时
            course.setSections(courseSectionMapper.getSectionsByCourseId(courseId));
            
            // 获取课程评价
            course.setReviews(courseReviewMapper.getReviewsByCourseId(courseId));
            
            return Result.success(course);
        } catch (Exception e) {
            return Result.error("获取课程详情失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<CourseSearchResponse> searchCourses(CourseSearchRequest request) {
        try {
            List<Course> courses = courseMapper.getCourseList(request);
            Integer total = courseMapper.getCourseCount(request);
            CourseSearchResponse response = new CourseSearchResponse(
                courses, 
                total, 
                request.getPageNum(), 
                request.getPageSize()
            );
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("搜索课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<Course>> getHotCourses(Integer limit) {
        try {
            List<Course> courses = courseMapper.getHotCourses(limit);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取热门课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<Course>> getNewCourses(Integer limit) {
        try {
            List<Course> courses = courseMapper.getNewCourses(limit);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取最新课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<Course>> getCoursesByCategory(Integer categoryId, Integer limit) {
        try {
            List<Course> courses = courseMapper.getCoursesByCategory(categoryId, limit);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取分类课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<CourseSection>> getCourseOutline(Integer courseId) {
        try {
            // 检查课程是否存在
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 获取课程章节
            List<CourseSection> sections = courseSectionMapper.getSectionsByCourseId(courseId);
            
            // 为每个章节加载课时
            for (CourseSection section : sections) {
                List<CourseLesson> lessons = courseLessonMapper.getLessonsBySectionId(section.getId());
                section.setLessons(lessons);
            }
            
            return Result.success(sections);
        } catch (Exception e) {
            return Result.error("获取课程大纲失败: " + e.getMessage());
        }
    }
}
