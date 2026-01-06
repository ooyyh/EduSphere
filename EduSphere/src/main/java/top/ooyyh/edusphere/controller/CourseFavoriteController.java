package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.service.CourseFavoriteService;
import top.ooyyh.edusphere.utils.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * 课程收藏控制器
 */
@RestController
@RequestMapping("/favorites")
@CrossOrigin
public class CourseFavoriteController {

    @Autowired
    private CourseFavoriteService favoriteService;

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return (Integer) userIdObj;
        }
        return -1;
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping
    public Result getUserFavorites(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return favoriteService.getUserFavorites(userId);
    }

    /**
     * 添加课程收藏
     */
    @PostMapping("/add/{courseId}")
    public Result addFavorite(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return favoriteService.addFavorite(userId, courseId);
    }

    /**
     * 移除课程收藏
     */
    @DeleteMapping("/remove/{courseId}")
    public Result removeFavorite(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return favoriteService.removeFavorite(userId, courseId);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{courseId}")
    public Result<Boolean> isFavorited(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.success(false);
        }
        return favoriteService.isFavorited(userId, courseId);
    }

    /**
     * 获取课程收藏数
     */
    @GetMapping("/count/{courseId}")
    public Result<Integer> getFavoriteCount(@PathVariable Integer courseId) {
        return favoriteService.getFavoriteCount(courseId);
    }
}
