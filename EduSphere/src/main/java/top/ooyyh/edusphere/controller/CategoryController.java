package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.Category;
import top.ooyyh.edusphere.service.CategoryService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }
}
