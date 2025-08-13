package com.krushit.moneymate.service;

import com.krushit.moneymate.dto.IncomeDTO;
import com.krushit.moneymate.entity.Category;
import com.krushit.moneymate.entity.Income;
import com.krushit.moneymate.entity.User;
import com.krushit.moneymate.repository.CategoryRepository;
import com.krushit.moneymate.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO dto) {
        User profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income newExpense = toEntity(dto, profile, category);
        newExpense = incomeRepository.save(newExpense);
        return toDTO(newExpense);
    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Income> list = incomeRepository.findByProfileIdAndDateBetween(profile.getUserId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId) {
        User profile = profileService.getCurrentProfile();
        Income entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!entity.getProfile().getUserId().equals(profile.getUserId())) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(entity);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        List<Income> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getUserId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getUserId());
        return total != null ? total: BigDecimal.ZERO;
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        User profile = profileService.getCurrentProfile();
        List<Income> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getUserId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    private Income toEntity(IncomeDTO dto, User profile, Category category) {
        return Income.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(Income entity) {
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
