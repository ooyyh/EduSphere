package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseSection {
    private Integer id;
    private Integer courseId;
    private String title;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联数据
    private List<CourseLesson> lessons;
}
