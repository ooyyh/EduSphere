package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.Cart;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.Order;
import top.ooyyh.edusphere.service.CartService;
import top.ooyyh.edusphere.service.OrderService;
import top.ooyyh.edusphere.service.UserCourseService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/user")
public class UserCenterController {
    
    @Autowired
    private UserCourseService userCourseService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取用户购买的课程
     */
    @GetMapping("/courses")
    public Result<List<Course>> getUserCourses(@RequestHeader("X-User-Id") Integer userId) {
        return userCourseService.getUserCourses(userId);
    }
    
    /**
     * 检查用户是否已购买课程
     */
    @GetMapping("/courses/{courseId}/purchased")
    public Result<Boolean> hasUserPurchased(@RequestHeader("X-User-Id") Integer userId, 
                                          @PathVariable Integer courseId) {
        return userCourseService.hasUserPurchased(userId, courseId);
    }
    
    /**
     * 购买课程
     */
    @PostMapping("/courses/{courseId}/purchase")
    public Result<String> purchaseCourse(@RequestHeader("X-User-Id") Integer userId,
                                       @PathVariable Integer courseId) {
        return userCourseService.purchaseCourse(userId, courseId);
    }
    
    /**
     * 获取用户购物车
     */
    @GetMapping("/cart")
    public Result<List<Cart>> getUserCart(@RequestHeader("X-User-Id") Integer userId) {
        return cartService.getUserCart(userId);
    }
    
    /**
     * 添加到购物车
     */
    @PostMapping("/cart/{courseId}")
    public Result<String> addToCart(@RequestHeader("X-User-Id") Integer userId,
                                  @PathVariable Integer courseId) {
        return cartService.addToCart(userId, courseId);
    }
    
    /**
     * 从购物车移除
     */
    @DeleteMapping("/cart/{courseId}")
    public Result<String> removeFromCart(@RequestHeader("X-User-Id") Integer userId,
                                       @PathVariable Integer courseId) {
        return cartService.removeFromCart(userId, courseId);
    }
    
    /**
     * 清空购物车
     */
    @DeleteMapping("/cart")
    public Result<String> clearCart(@RequestHeader("X-User-Id") Integer userId) {
        return cartService.clearCart(userId);
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/orders")
    public Result<List<Order>> getUserOrders(@RequestHeader("X-User-Id") Integer userId) {
        return orderService.getUserOrders(userId);
    }
    
    /**
     * 根据订单号查询订单
     */
    @GetMapping("/orders/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        return orderService.getOrderByOrderNo(orderNo);
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/orders/{orderNo}/pay")
    public Result<String> payOrder(@PathVariable String orderNo) {
        return orderService.payOrder(orderNo);
    }
}
