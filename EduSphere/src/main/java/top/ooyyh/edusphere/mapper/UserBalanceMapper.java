package top.ooyyh.edusphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.UserBalance;

import java.math.BigDecimal;

/**
 * 用户余额Mapper接口
 */
@Mapper
public interface UserBalanceMapper extends BaseMapper<UserBalance> {
    
    /**
     * 根据用户ID获取余额
     */
    UserBalance getByUserId(@Param("userId") Integer userId);
    
    /**
     * 更新用户余额
     */
    int updateBalance(@Param("userId") Integer userId, 
                     @Param("balance") BigDecimal balance,
                     @Param("totalRecharge") BigDecimal totalRecharge,
                     @Param("totalConsumption") BigDecimal totalConsumption);
    
    /**
     * 增加用户余额
     */
    int addBalance(@Param("userId") Integer userId, 
                  @Param("amount") BigDecimal amount);
    
    /**
     * 减少用户余额
     */
    int subtractBalance(@Param("userId") Integer userId, 
                       @Param("amount") BigDecimal amount);
}
