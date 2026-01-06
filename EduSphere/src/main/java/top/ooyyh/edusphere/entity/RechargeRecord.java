package top.ooyyh.edusphere.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录实体类
 */
@Data
@TableName("recharge_record")
public class RechargeRecord {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 充值前余额
     */
    private BigDecimal balanceBefore;
    
    /**
     * 充值后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 充值类型：manual-手动充值，system-系统充值
     */
    private String rechargeType;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
