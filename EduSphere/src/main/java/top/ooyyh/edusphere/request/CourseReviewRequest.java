package top.ooyyh.edusphere.request;

import lombok.Data;

@Data
public class CourseReviewRequest {
    private Integer courseId;
    private Integer rating;
    private String content;
}
