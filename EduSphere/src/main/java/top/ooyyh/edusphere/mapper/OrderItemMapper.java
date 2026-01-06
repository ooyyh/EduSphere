package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.OrderItem;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    // 添加订单项
    int addOrderItem(OrderItem orderItem);
    
    // 根据订单ID查询订单项
    List<OrderItem> getOrderItemsByOrderId(@Param("orderId") Integer orderId);
}
