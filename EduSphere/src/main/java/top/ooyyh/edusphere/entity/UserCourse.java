package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCourse {
    private Integer id;
    private Integer userId;
    private Integer courseId;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseTime;
    private Integer status;
    private LocalDateTime createdAt;
}
