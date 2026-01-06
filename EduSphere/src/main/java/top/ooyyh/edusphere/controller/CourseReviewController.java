package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.CourseReview;
import top.ooyyh.edusphere.request.CourseReviewRequest;
import top.ooyyh.edusphere.service.CourseReviewService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/review")
public class CourseReviewController {
    
    @Autowired
    private CourseReviewService courseReviewService;
    
    /**
     * 获取课程评价列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<CourseReview>> getCourseReviews(@PathVariable Integer courseId) {
        return courseReviewService.getCourseReviews(courseId);
    }
    
    /**
     * 添加课程评价
     */
    @PostMapping("/course/{courseId}")
    public Result<String> addCourseReview(@PathVariable Integer courseId, @RequestBody CourseReviewRequest request, 
                                        @RequestHeader("X-User-Id") Integer userId) {
        // 设置课程ID
        request.setCourseId(courseId);
        return courseReviewService.addCourseReview(request, userId);
    }
    
    /**
     * 更新课程评价
     */
    @PutMapping("/update")
    public Result<String> updateCourseReview(@RequestBody CourseReviewRequest request,
                                           @RequestHeader("X-User-Id") Integer userId) {
        return courseReviewService.updateCourseReview(request, userId);
    }

    /**
     * 点赞课程评价
     */
    @PostMapping("/{reviewId}/like")
    public Result<String> likeReview(@PathVariable Integer reviewId, @RequestHeader("X-User-Id") Integer userId) {
        return courseReviewService.likeReview(reviewId);
    }
}
