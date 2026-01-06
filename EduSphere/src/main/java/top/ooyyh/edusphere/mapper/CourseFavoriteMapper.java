package top.ooyyh.edusphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.CourseFavorite;

import java.util.List;

@Mapper
public interface CourseFavoriteMapper extends BaseMapper<CourseFavorite> {

    /**
     * 获取用户收藏的课程列表
     */
    List<CourseFavorite> getUserFavorites(@Param("userId") Integer userId);

    /**
     * 添加课程收藏
     */
    int addFavorite(CourseFavorite favorite);

    /**
     * 移除课程收藏
     */
    int removeFavorite(@Param("userId") Integer userId, @Param("courseId") Integer courseId);

    /**
     * 检查是否已收藏
     */
    Boolean isFavorited(@Param("userId") Integer userId, @Param("courseId") Integer courseId);

    /**
     * 获取课程收藏数
     */
    int getFavoriteCount(@Param("courseId") Integer courseId);
}
