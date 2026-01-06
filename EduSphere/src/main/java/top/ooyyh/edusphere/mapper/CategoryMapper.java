package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.ooyyh.edusphere.entity.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {
    // 查询所有分类
    List<Category> getAllCategories();
    
    // 根据ID查询分类
    Category getCategoryById(Integer id);
    
    // 根据slug查询分类
    Category getCategoryBySlug(String slug);
}
