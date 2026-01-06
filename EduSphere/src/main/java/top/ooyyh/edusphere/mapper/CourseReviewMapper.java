package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.CourseReview;

import java.util.List;

@Mapper
public interface CourseReviewMapper {
    // 根据课程ID查询评价列表
    List<CourseReview> getReviewsByCourseId(@Param("courseId") Integer courseId);
    
    // 添加课程评价
    int addReview(CourseReview review);
    
    // 检查用户是否已评价该课程
    boolean hasUserReviewed(@Param("courseId") Integer courseId, @Param("userId") Integer userId);
    
    // 更新课程评价
    int updateReview(CourseReview review);
    
    // 点赞评论
    int likeReview(@Param("reviewId") Integer reviewId);
}
