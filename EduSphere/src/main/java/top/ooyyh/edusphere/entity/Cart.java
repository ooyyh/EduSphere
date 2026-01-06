package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer courseId;
    private LocalDateTime createdAt;
    
    // 关联数据
    private Course course;
}
