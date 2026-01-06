package top.ooyyh.edusphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.InstructorIncome;

import java.math.BigDecimal;
import java.util.List;

/**
 * 讲师收入记录Mapper接口
 */
@Mapper
public interface InstructorIncomeMapper extends BaseMapper<InstructorIncome> {
    
    /**
     * 根据讲师ID获取收入记录
     */
    List<InstructorIncome> getByInstructorId(@Param("instructorId") Integer instructorId);
    
    /**
     * 根据讲师ID获取总收入
     */
    BigDecimal getTotalIncomeByInstructorId(@Param("instructorId") Integer instructorId);
    
    /**
     * 根据讲师ID获取收入记录（分页）
     */
    List<InstructorIncome> getByInstructorIdWithPage(@Param("instructorId") Integer instructorId,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);
    
    /**
     * 统计讲师收入记录数量
     */
    int countByInstructorId(@Param("instructorId") Integer instructorId);
}
