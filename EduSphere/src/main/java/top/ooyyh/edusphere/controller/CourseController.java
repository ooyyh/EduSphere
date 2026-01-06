package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.CourseSection;
import top.ooyyh.edusphere.request.CourseSearchRequest;
import top.ooyyh.edusphere.response.CourseSearchResponse;
import top.ooyyh.edusphere.service.CourseService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/course")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public Result<Course> getCourseDetail(@PathVariable Integer id) {
        return courseService.getCourseDetail(id);
    }
    
    /**
     * 搜索课程
     */
    @PostMapping("/search")
    public Result<CourseSearchResponse> searchCourses(@RequestBody CourseSearchRequest request) {
        return courseService.searchCourses(request);
    }
    
    /**
     * 获取热门课程
     */
    @GetMapping("/hot")
    public Result<List<Course>> getHotCourses(@RequestParam(defaultValue = "6") Integer limit) {
        return courseService.getHotCourses(limit);
    }
    
    /**
     * 获取最新课程
     */
    @GetMapping("/new")
    public Result<List<Course>> getNewCourses(@RequestParam(defaultValue = "6") Integer limit) {
        return courseService.getNewCourses(limit);
    }
    
    /**
     * 根据分类获取课程
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Course>> getCoursesByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "6") Integer limit) {
        return courseService.getCoursesByCategory(categoryId, limit);
    }
    
    /**
     * 获取课程大纲（章节和课时）
     */
    @GetMapping("/{courseId}/outline")
    public Result<List<CourseSection>> getCourseOutline(@PathVariable Integer courseId) {
        return courseService.getCourseOutline(courseId);
    }
}
