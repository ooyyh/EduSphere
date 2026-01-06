package top.ooyyh.edusphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.RechargeRecord;

import java.util.List;

/**
 * 充值记录Mapper接口
 */
@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {
    
    /**
     * 根据用户ID获取充值记录
     */
    List<RechargeRecord> getByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID获取充值记录（分页）
     */
    List<RechargeRecord> getByUserIdWithPage(@Param("userId") Integer userId,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);
    
    /**
     * 统计用户充值记录数量
     */
    int countByUserId(@Param("userId") Integer userId);
}
