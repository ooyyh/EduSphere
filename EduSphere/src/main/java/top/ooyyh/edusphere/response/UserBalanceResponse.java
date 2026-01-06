package top.ooyyh.edusphere.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户余额响应DTO
 */
@Data
public class UserBalanceResponse {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户余额
     */
    private BigDecimal balance;
    
    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;
    
    /**
     * 累计充值
     */
    private BigDecimal totalRecharge;
    
    /**
     * 累计消费
     */
    private BigDecimal totalConsumption;
    
    /**
     * 可用余额
     */
    private BigDecimal availableBalance;
}
