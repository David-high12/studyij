package com.example.labmanagement.service;

import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final OperationLogService logService;

    public CategoryService(CategoryRepository categoryRepository, OperationLogService logService) {
        this.categoryRepository = categoryRepository;
        this.logService = logService;
    }

    public List<Category> findAll(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword.trim());
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("分类不存在"));
    }

    @Transactional
    public Category save(Category form, UserAccount operator, String ipAddress) {
        Category category = form.getId() == null ? new Category() : findById(form.getId());
        if (form.getId() == null && categoryRepository.existsByCategoryName(form.getCategoryName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        if (form.getId() != null && !category.getCategoryName().equals(form.getCategoryName()) && categoryRepository.existsByCategoryName(form.getCategoryName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        category.setCategoryName(form.getCategoryName().trim());
        category.setDescription(form.getDescription());
        Category saved = categoryRepository.save(category);
        logService.record(operator, "分类管理", "保存分类：" + saved.getCategoryName(), ipAddress);
        return saved;
    }

    @Transactional
    public void delete(Long id, UserAccount operator, String ipAddress) {
        Category category = findById(id);
        categoryRepository.delete(category);
        logService.record(operator, "分类管理", "删除分类：" + category.getCategoryName(), ipAddress);
    }
}
