package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Course {
    private Integer id;
    private String title;
    private String subtitle;
    private String description;
    private String coverImage;
    private Integer instructorId;
    private Integer categoryId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean isFree;
    private String level;
    private String duration;
    private Integer studentCount;
    private BigDecimal rating;
    private Integer ratingCount;
    private Boolean isHot;
    private Boolean isNew;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联数据
    private User instructor;
    private Category category;
    private List<CourseSection> sections;
    private List<CourseReview> reviews;
}
