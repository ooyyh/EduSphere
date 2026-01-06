package top.ooyyh.edusphere.config;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ooyyh.edusphere.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别的注解
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        
        // 如果没有@RequireRole注解，直接通过
        if (requireRole == null) {
            return true;
        }
        
        // 获取用户角色
        String userRole = (String) request.getAttribute("role");
        if (userRole == null) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "未登录或登录已过期");
            return false;
        }
        
        // 检查角色权限
        String[] allowedRoles = requireRole.value();
        for (String allowedRole : allowedRoles) {
            if (allowedRole.equals(userRole)) {
                return true;
            }
        }
        
        // 权限不足
        ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, 
            "权限不足，需要角色：" + String.join("或", allowedRoles));
        return false;
    }
}
