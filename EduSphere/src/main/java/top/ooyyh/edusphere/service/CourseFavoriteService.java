package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.utils.Result;

public interface CourseFavoriteService {

    /**
     * 获取用户收藏列表
     */
    Result getUserFavorites(Integer userId);

    /**
     * 添加课程收藏
     */
    Result addFavorite(Integer userId, Integer courseId);

    /**
     * 移除课程收藏
     */
    Result removeFavorite(Integer userId, Integer courseId);

    /**
     * 检查是否已收藏
     */
    Result<Boolean> isFavorited(Integer userId, Integer courseId);

    /**
     * 获取课程收藏数
     */
    Result<Integer> getFavoriteCount(Integer courseId);
}
