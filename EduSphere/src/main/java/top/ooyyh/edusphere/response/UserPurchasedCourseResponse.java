package top.ooyyh.edusphere.response;

import lombok.Data;
import top.ooyyh.edusphere.entity.PurchaseRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户购买课程响应DTO
 * 扩展购买记录，包含课程相关信息
 */
@Data
public class UserPurchasedCourseResponse {
    
    /**
     * 购买记录ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 讲师ID
     */
    private Integer instructorId;
    
    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;
    
    /**
     * 购买前余额
     */
    private BigDecimal balanceBefore;
    
    /**
     * 购买后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 讲师收入
     */
    private BigDecimal instructorIncome;
    
    /**
     * 平台手续费
     */
    private BigDecimal platformFee;
    
    /**
     * 购买状态
     */
    private String status;
    
    /**
     * 购买时间
     */
    private LocalDateTime createdAt;
    
    // 以下为扩展字段，包含课程相关信息
    /**
     * 课程标题
     */
    private String courseTitle;
    
    /**
     * 课程封面
     */
    private String coverImage;
    
    /**
     * 讲师名称
     */
    private String instructorName;
}