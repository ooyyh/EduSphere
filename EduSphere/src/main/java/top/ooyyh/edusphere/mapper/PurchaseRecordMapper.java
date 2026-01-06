package top.ooyyh.edusphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.PurchaseRecord;

import java.util.List;

/**
 * 购买记录Mapper接口
 */
@Mapper
public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
    
    /**
     * 根据用户ID获取购买记录
     */
    List<PurchaseRecord> getByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据讲师ID获取销售记录
     */
    List<PurchaseRecord> getByInstructorId(@Param("instructorId") Integer instructorId);
    
    /**
     * 根据用户ID和课程ID获取购买记录
     */
    PurchaseRecord getByUserIdAndCourseId(@Param("userId") Integer userId, 
                                        @Param("courseId") Integer courseId);
    
    /**
     * 根据用户ID获取购买记录（分页）
     */
    List<PurchaseRecord> getByUserIdWithPage(@Param("userId") Integer userId,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);
    
    /**
     * 统计用户购买记录数量
     */
    int countByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计讲师销售记录数量
     */
    int countByInstructorId(@Param("instructorId") Integer instructorId);
}
