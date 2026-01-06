package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ooyyh.edusphere.entity.CourseFavorite;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.mapper.CourseFavoriteMapper;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.service.CourseFavoriteService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@Service
public class CourseFavoriteServiceImpl implements CourseFavoriteService {

    @Autowired
    private CourseFavoriteMapper favoriteMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Result getUserFavorites(Integer userId) {
        try {
            List<CourseFavorite> favorites = favoriteMapper.getUserFavorites(userId);
            return Result.success(favorites);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取收藏列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result addFavorite(Integer userId, Integer courseId) {
        try {
            // 检查课程是否存在
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }

            // 检查是否已收藏
            Boolean isFavorited = favoriteMapper.isFavorited(userId, courseId);
            if (isFavorited != null && isFavorited) {
                return Result.error("已经收藏过该课程");
            }

            // 添加收藏
            CourseFavorite favorite = new CourseFavorite();
            favorite.setUserId(userId);
            favorite.setCourseId(courseId);
            favoriteMapper.addFavorite(favorite);

            return Result.success("收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("收藏失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result removeFavorite(Integer userId, Integer courseId) {
        try {
            // 检查是否已收藏
            Boolean isFavorited = favoriteMapper.isFavorited(userId, courseId);
            if (isFavorited == null || !isFavorited) {
                return Result.error("未收藏该课程");
            }

            // 移除收藏
            favoriteMapper.removeFavorite(userId, courseId);

            return Result.success("取消收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("取消收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> isFavorited(Integer userId, Integer courseId) {
        try {
            Boolean isFavorited = favoriteMapper.isFavorited(userId, courseId);
            return Result.success(isFavorited != null && isFavorited);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("检查收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> getFavoriteCount(Integer courseId) {
        try {
            int count = favoriteMapper.getFavoriteCount(courseId);
            return Result.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取收藏数失败: " + e.getMessage());
        }
    }
}
