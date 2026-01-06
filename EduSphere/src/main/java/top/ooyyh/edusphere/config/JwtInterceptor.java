package top.ooyyh.edusphere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ooyyh.edusphere.mapper.UserMapper;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.utils.JwtUtils;
import top.ooyyh.edusphere.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理跨域预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 不需要认证的路径
        if (isPublicPath(requestURI)) {
            return true;
        }
        
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未提供有效的认证信息");
            return false;
        }
        
        try {
            // 提取token
            String token = authHeader.substring(7);
            
            // 验证token
            String username = JwtUtils.getUsername(token);
            String role = JwtUtils.getRole(token);
            
            // 获取用户ID
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
                return false;
            }
            
            // 将用户信息添加到请求头中，供后续使用
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            request.setAttribute("userId", userId);
            
            return true;
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
            return false;
        }
    }
    
    /**
     * 判断是否为公开路径
     */
    private boolean isPublicPath(String requestURI) {
        boolean isPublic = requestURI.startsWith("/api/user/login") ||
               requestURI.startsWith("/api/user/register") ||
               requestURI.startsWith("/api/course/") ||
               requestURI.startsWith("/api/category/") ||
               requestURI.startsWith("/api/home/") ||
               requestURI.startsWith("/api/review/course/") ||
               requestURI.startsWith("/api/placeholder/") ||
               requestURI.startsWith("/api/static/") ||
               requestURI.startsWith("/api/images/");
        
        return isPublic;
    }
    
    /**
     * 从token中获取用户ID（从数据库查询）
     */
    private Integer getUserIdFromToken(String token) {
        try {
            String username = JwtUtils.getUsername(token);
            if (username != null && !username.isEmpty()) {
                // 从数据库查询用户信息
                User user = userMapper.getUserByUsername(username);
                if (user != null) {
                    return user.getId();
                }
            }
        } catch (Exception e) {
            // 如果解析失败，记录日志
            System.err.println("获取用户ID失败: " + e.getMessage());
        }
        return null; // 如果查询失败，返回null
    }
}
