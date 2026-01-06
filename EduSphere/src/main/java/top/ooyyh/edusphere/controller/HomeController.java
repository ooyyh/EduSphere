package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.Category;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.service.CategoryService;
import top.ooyyh.edusphere.service.CourseService;
import top.ooyyh.edusphere.utils.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/home")
public class HomeController {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取首页数据
     */
    @GetMapping("/data")
    public Result<Map<String, Object>> getHomeData() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取热门课程
            Result<List<Course>> hotCoursesResult = courseService.getHotCourses(4);
            if (hotCoursesResult.getCode() == 0) {
                data.put("hotCourses", hotCoursesResult.getData());
            }
            
            // 获取最新课程
            Result<List<Course>> newCoursesResult = courseService.getNewCourses(4);
            if (newCoursesResult.getCode() == 0) {
                data.put("newCourses", newCoursesResult.getData());
            }
            
            // 获取所有分类
            Result<List<Category>> categoriesResult = categoryService.getAllCategories();
            if (categoriesResult.getCode() == 0) {
                data.put("categories", categoriesResult.getData());
            }
            
            // 统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCourses", "1000+");
            stats.put("totalStudents", "50000+");
            stats.put("totalInstructors", "500+");
            stats.put("totalHours", "100000+");
            data.put("stats", stats);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取首页数据失败: " + e.getMessage());
        }
    }
}
