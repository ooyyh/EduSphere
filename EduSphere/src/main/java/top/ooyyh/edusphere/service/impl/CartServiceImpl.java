package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Cart;
import top.ooyyh.edusphere.mapper.CartMapper;
import top.ooyyh.edusphere.service.CartService;
import top.ooyyh.edusphere.utils.Result;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Override
    public Result<List<Cart>> getUserCart(Integer userId) {
        try {
            List<Cart> cartItems = cartMapper.getUserCart(userId);
            return Result.success(cartItems);
        } catch (Exception e) {
            return Result.error("获取购物车失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> addToCart(Integer userId, Integer courseId) {
        try {
            // 检查是否已在购物车中
            if (cartMapper.isInCart(userId, courseId)) {
                return Result.error("课程已在购物车中");
            }
            
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setCourseId(courseId);
            cart.setCreatedAt(LocalDateTime.now());
            
            int result = cartMapper.addToCart(cart);
            if (result > 0) {
                return Result.success("已添加到购物车");
            } else {
                return Result.error("添加到购物车失败");
            }
        } catch (Exception e) {
            return Result.error("添加到购物车失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> removeFromCart(Integer userId, Integer courseId) {
        try {
            int result = cartMapper.removeFromCart(userId, courseId);
            if (result > 0) {
                return Result.success("已从购物车移除");
            } else {
                return Result.error("移除失败");
            }
        } catch (Exception e) {
            return Result.error("从购物车移除失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> clearCart(Integer userId) {
        try {
            int result = cartMapper.clearCart(userId);
            return Result.success("购物车已清空");
        } catch (Exception e) {
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }
}
