package top.ooyyh.edusphere.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer courseId;
    private String courseTitle;
    private BigDecimal coursePrice;
    private LocalDateTime createdAt;
}
