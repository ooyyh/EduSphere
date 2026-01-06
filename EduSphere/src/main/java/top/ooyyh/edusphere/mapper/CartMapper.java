package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.Cart;

import java.util.List;

@Mapper
public interface CartMapper {
    // 获取用户购物车
    List<Cart> getUserCart(@Param("userId") Integer userId);
    
    // 添加到购物车
    int addToCart(Cart cart);
    
    // 从购物车移除
    int removeFromCart(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
    
    // 清空购物车
    int clearCart(@Param("userId") Integer userId);
    
    // 检查课程是否在购物车中
    boolean isInCart(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
}
