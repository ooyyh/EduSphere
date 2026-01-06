package top.ooyyh.edusphere.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买响应DTO
 */
@Data
public class PurchaseResponse {
    
    /**
     * 购买记录ID
     */
    private Integer purchaseRecordId;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 课程标题
     */
    private String courseTitle;
    
    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;
    
    /**
     * 购买后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;
    
    /**
     * 购买状态
     */
    private String status;
}
