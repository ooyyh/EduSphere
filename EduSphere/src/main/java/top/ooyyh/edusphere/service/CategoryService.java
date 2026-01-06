package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.Category;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

public interface CategoryService {
    // 获取所有分类
    Result<List<Category>> getAllCategories();
    
    // 根据ID获取分类
    Result<Category> getCategoryById(Integer id);
}
