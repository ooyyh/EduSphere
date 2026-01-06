package top.ooyyh.edusphere.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 讲师收入记录实体类
 */
@Data
@TableName("instructor_income")
public class InstructorIncome {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 讲师ID
     */
    private Integer instructorId;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 购买记录ID
     */
    private Integer purchaseRecordId;
    
    /**
     * 收入金额
     */
    private BigDecimal incomeAmount;
    
    /**
     * 累计收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
