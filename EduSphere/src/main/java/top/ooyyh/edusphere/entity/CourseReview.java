package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseReview {
    private Integer id;
    private Integer courseId;
    private Integer userId;
    private Integer rating;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    
    // 关联数据
    private User user;
}
