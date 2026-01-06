package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.CourseSection;
import top.ooyyh.edusphere.request.CourseSearchRequest;
import top.ooyyh.edusphere.response.CourseSearchResponse;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface CourseService {
    // 获取课程详情
    Result<Course> getCourseDetail(Integer courseId);
    
    // 搜索课程
    Result<CourseSearchResponse> searchCourses(CourseSearchRequest request);
    
    // 获取热门课程
    Result<List<Course>> getHotCourses(Integer limit);
    
    // 获取最新课程
    Result<List<Course>> getNewCourses(Integer limit);
    
    // 根据分类获取课程
    Result<List<Course>> getCoursesByCategory(Integer categoryId, Integer limit);
    
    // 获取课程大纲（章节和课时）
    Result<List<CourseSection>> getCourseOutline(Integer courseId);
}
