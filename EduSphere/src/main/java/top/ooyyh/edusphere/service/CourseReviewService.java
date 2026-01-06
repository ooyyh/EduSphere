package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.CourseReview;
import top.ooyyh.edusphere.request.CourseReviewRequest;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface CourseReviewService {
    // 获取课程评价列表
    Result<List<CourseReview>> getCourseReviews(Integer courseId);
    
    // 添加课程评价
    Result<String> addCourseReview(CourseReviewRequest request, Integer userId);
    
    // 更新课程评价
    Result<String> updateCourseReview(CourseReviewRequest request, Integer userId);
    
    // 点赞评论
    Result<String> likeReview(Integer reviewId);
}
