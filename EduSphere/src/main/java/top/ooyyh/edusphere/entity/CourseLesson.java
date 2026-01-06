package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseLesson {
    private Integer id;
    private Integer sectionId;
    private String title;
    private String description;
    private String type;
    private String duration;
    private String videoUrl;
    private String documentUrl;
    private Boolean isFree;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
