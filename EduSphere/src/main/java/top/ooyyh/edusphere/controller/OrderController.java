package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.service.OrderService;
import top.ooyyh.edusphere.utils.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取当前用户ID（从JWT中解析）
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return (Integer) userIdObj;
        }
        return -1;
    }

    /**
     * 创建订单
     * 请求体示例：{"courseIds": [1, 2, 3]}
     */
    @PostMapping("/create")
    public Result createOrder(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }

        List<Integer> courseIds = (List<Integer>) request.get("courseIds");
        if (courseIds == null || courseIds.isEmpty()) {
            return Result.error("请选择要购买的课程");
        }

        return orderService.createOrder(userId, courseIds);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public Result getUserOrders(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return orderService.getUserOrders(userId);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderNo}")
    public Result getOrderDetail(@PathVariable String orderNo) {
        return orderService.getOrderByOrderNo(orderNo);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderNo}")
    public Result payOrder(@PathVariable String orderNo) {
        return orderService.payOrder(orderNo);
    }
}
