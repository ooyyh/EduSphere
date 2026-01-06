package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Category;
import top.ooyyh.edusphere.mapper.CategoryMapper;
import top.ooyyh.edusphere.service.CategoryService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public Result<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryMapper.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Category> getCategoryById(Integer id) {
        try {
            Category category = categoryMapper.getCategoryById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            return Result.error("获取分类详情失败: " + e.getMessage());
        }
    }
}
