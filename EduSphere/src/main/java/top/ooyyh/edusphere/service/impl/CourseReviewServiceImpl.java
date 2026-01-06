package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.CourseReview;
import top.ooyyh.edusphere.mapper.CourseReviewMapper;
import top.ooyyh.edusphere.request.CourseReviewRequest;
import top.ooyyh.edusphere.service.CourseReviewService;
import top.ooyyh.edusphere.utils.Result;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseReviewServiceImpl implements CourseReviewService {
    
    @Autowired
    private CourseReviewMapper courseReviewMapper;
    
    @Override
    public Result<List<CourseReview>> getCourseReviews(Integer courseId) {
        try {
            List<CourseReview> reviews = courseReviewMapper.getReviewsByCourseId(courseId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error("获取课程评价失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> addCourseReview(CourseReviewRequest request, Integer userId) {
        try {
            // 检查用户是否已评价该课程
            if (courseReviewMapper.hasUserReviewed(request.getCourseId(), userId)) {
                return Result.error("您已经评价过该课程");
            }
            
            CourseReview review = new CourseReview();
            review.setCourseId(request.getCourseId());
            review.setUserId(userId);
            review.setRating(request.getRating());
            review.setContent(request.getContent());
            review.setStatus(1);
            review.setCreatedAt(LocalDateTime.now());
            
            int result = courseReviewMapper.addReview(review);
            if (result > 0) {
                return Result.success("评价成功");
            } else {
                return Result.error("评价失败");
            }
        } catch (Exception e) {
            return Result.error("添加评价失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> updateCourseReview(CourseReviewRequest request, Integer userId) {
        try {
            CourseReview review = new CourseReview();
            review.setCourseId(request.getCourseId());
            review.setUserId(userId);
            review.setRating(request.getRating());
            review.setContent(request.getContent());
            review.setUpdatedAt(LocalDateTime.now());
            
            int result = courseReviewMapper.updateReview(review);
            if (result > 0) {
                return Result.success("更新评价成功");
            } else {
                return Result.error("更新评价失败");
            }
        } catch (Exception e) {
            return Result.error("更新评价失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> likeReview(Integer reviewId) {
        try {
            int result = courseReviewMapper.likeReview(reviewId);
            if (result > 0) {
                return Result.success("点赞成功");
            } else {
                return Result.error("点赞失败");
            }
        } catch (Exception e) {
            return Result.error("点赞失败: " + e.getMessage());
        }
    }
}
