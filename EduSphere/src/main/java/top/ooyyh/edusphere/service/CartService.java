package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Cart;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface CartService {
    // 获取用户购物车
    Result<List<Cart>> getUserCart(Integer userId);
    
    // 添加到购物车
    Result<String> addToCart(Integer userId, Integer courseId);
    
    // 从购物车移除
    Result<String> removeFromCart(Integer userId, Integer courseId);
    
    // 清空购物车
    Result<String> clearCart(Integer userId);
}
