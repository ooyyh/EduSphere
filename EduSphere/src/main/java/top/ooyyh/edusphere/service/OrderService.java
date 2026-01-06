package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Order;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface OrderService {
    // 创建订单
    Result<Order> createOrder(Integer userId, List<Integer> courseIds);
    
    // 获取用户订单列表
    Result<List<Order>> getUserOrders(Integer userId);
    
    // 根据订单号查询订单
    Result<Order> getOrderByOrderNo(String orderNo);
    
    // 支付订单
    Result<String> payOrder(String orderNo);
}
