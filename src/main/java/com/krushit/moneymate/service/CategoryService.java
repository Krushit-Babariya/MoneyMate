package com.krushit.moneymate.service;

import com.krushit.moneymate.dto.CategoryDTO;
import com.krushit.moneymate.entity.Category;
import com.krushit.moneymate.entity.User;
import com.krushit.moneymate.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        User profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getUserId())) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepository.findByProfileId(profile.getUserId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        User profile = profileService.getCurrentProfile();
        List<Category> entities = categoryRepository.findByTypeAndProfileId(type, profile.getUserId());
        return entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
        User profile = profileService.getCurrentProfile();
        Category existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getUserId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    private Category toEntity(CategoryDTO categoryDTO, User profile) {
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(Category entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ?  entity.getProfile().getUserId(): null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();
    }
}
