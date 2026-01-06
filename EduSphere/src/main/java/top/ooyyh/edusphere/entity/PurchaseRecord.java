package top.ooyyh.edusphere.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买记录实体类
 */
@Data
@TableName("purchase_record")
public class PurchaseRecord {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 购买用户ID
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
     * 购买状态：success-成功，failed-失败，refunded-已退款
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
