package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.Order;

import java.util.List;

@Mapper
public interface OrderMapper {
    // 创建订单
    int createOrder(Order order);
    
    // 根据ID查询订单
    Order getOrderById(@Param("id") Integer id);
    
    // 根据订单号查询订单
    Order getOrderByOrderNo(@Param("orderNo") String orderNo);
    
    // 获取用户订单列表
    List<Order> getUserOrders(@Param("userId") Integer userId);
    
    // 更新订单状态
    int updateOrderStatus(@Param("orderId") Integer orderId, @Param("status") String status);
}
