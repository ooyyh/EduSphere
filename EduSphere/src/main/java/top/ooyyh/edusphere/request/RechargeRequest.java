package top.ooyyh.edusphere.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 充值请求DTO
 */
@Data
public class RechargeRequest {
    
    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 备注
     */
    private String remark;
}
