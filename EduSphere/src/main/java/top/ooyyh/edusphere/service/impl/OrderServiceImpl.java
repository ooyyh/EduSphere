package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.Order;
import top.ooyyh.edusphere.entity.OrderItem;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.mapper.OrderItemMapper;
import top.ooyyh.edusphere.mapper.OrderMapper;
import top.ooyyh.edusphere.service.OrderService;
import top.ooyyh.edusphere.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public Result<Order> createOrder(Integer userId, List<Integer> courseIds) {
        try {
            // 计算订单总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Integer courseId : courseIds) {
                Course course = courseMapper.getCourseById(courseId);
                if (course != null) {
                    totalAmount = totalAmount.add(course.getPrice());
                }
            }
            
            // 创建订单
            Order order = new Order();
            order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setStatus("pending");
            order.setCreatedAt(LocalDateTime.now());
            
            int result = orderMapper.createOrder(order);
            if (result > 0) {
                // 创建订单项
                for (Integer courseId : courseIds) {
                    Course course = courseMapper.getCourseById(courseId);
                    if (course != null) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(order.getId());
                        orderItem.setCourseId(courseId);
                        orderItem.setCourseTitle(course.getTitle());
                        orderItem.setCoursePrice(course.getPrice());
                        orderItem.setCreatedAt(LocalDateTime.now());
                        orderItemMapper.addOrderItem(orderItem);
                    }
                }
                return Result.success(order);
            } else {
                return Result.error("创建订单失败");
            }
        } catch (Exception e) {
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<Order>> getUserOrders(Integer userId) {
        try {
            List<Order> orders = orderMapper.getUserOrders(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Order> getOrderByOrderNo(String orderNo) {
        try {
            Order order = orderMapper.getOrderByOrderNo(orderNo);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> payOrder(String orderNo) {
        try {
            Order order = orderMapper.getOrderByOrderNo(orderNo);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            if (!"pending".equals(order.getStatus())) {
                return Result.error("订单状态不正确");
            }
            
            int result = orderMapper.updateOrderStatus(order.getId(), "paid");
            if (result > 0) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error("支付失败: " + e.getMessage());
        }
    }
}
