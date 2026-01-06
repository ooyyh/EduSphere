package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.service.CartService;
import top.ooyyh.edusphere.utils.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

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
     * 获取用户购物车
     */
    @GetMapping
    public Result getUserCart(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return cartService.getUserCart(userId);
    }

    /**
     * 添加课程到购物车
     */
    @PostMapping("/add/{courseId}")
    public Result addToCart(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return cartService.addToCart(userId, courseId);
    }

    /**
     * 从购物车移除课程
     */
    @DeleteMapping("/remove/{courseId}")
    public Result removeFromCart(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return cartService.removeFromCart(userId, courseId);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result clearCart(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return cartService.clearCart(userId);
    }
}
