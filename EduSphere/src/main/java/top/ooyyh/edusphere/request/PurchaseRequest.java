package top.ooyyh.edusphere.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 购买课程请求DTO
 */
@Data
public class PurchaseRequest {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
}
