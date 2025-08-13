package com.krushit.moneymate.service;

import com.krushit.moneymate.dto.ExpenseDTO;
import com.krushit.moneymate.entity.Category;
import com.krushit.moneymate.entity.Expense;
import com.krushit.moneymate.entity.User;
import com.krushit.moneymate.repository.CategoryRepository;
import com.krushit.moneymate.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO dto) {
        User profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Expense newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> list = expenseRepository.findByProfileIdAndDateBetween(profile.getUserId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteExpense(Long expenseId) {
        User profile = profileService.getCurrentProfile();
        Expense entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!entity.getProfile().getUserId().equals(profile.getUserId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(entity);
    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        List<Expense> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getUserId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpenseForCurrentUser() {
        User profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(((User) profile).getUserId());
        return total != null ? total: BigDecimal.ZERO;
    }

    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        User profile = profileService.getCurrentProfile();
        List<Expense> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getUserId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<Expense> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDTO).toList();
    }

    private Expense toEntity(ExpenseDTO dto, User profile, Category category) {
        return Expense.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(Expense entity) {
        return ExpenseDTO.builder()
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
