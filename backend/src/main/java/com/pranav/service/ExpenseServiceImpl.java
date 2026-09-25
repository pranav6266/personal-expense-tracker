package com.pranav.service;

import com.pranav.model.AppUser;
import com.pranav.model.Expense;
import com.pranav.repository.ExpenseRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Profile("postgres-db")
public class ExpenseServiceImpl implements ExpenseService{

	private final ExpenseRepository expenseRepository;
	private final UserService userService;


    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

	@Override
	public List<Expense> getAllUserExpenses(Long userId) {
		return new ArrayList<>(expenseRepository.findByUserIdOrderByDateDesc(userId));
	}

	@Override
	public List<Expense> getExpenseByDay(String date, Long userId) {
		return expenseRepository.findByUserIdOrderByDateDesc(userId).
				stream().filter(expense ->
						expense.getDate().equals(date)).toList();
	}

	@Override
	public List<Expense> getExpenseByCategoryAndMonth(String category, String month, Long userId) {
		return expenseRepository.findByUserIdOrderByDateDesc(userId).
				stream().filter
						(expense -> expense.getCategory().equalsIgnoreCase(category) &&
						expense.getDate().startsWith(month)).toList() ;
	}

	@Override
	public List<String> getAllExpenseCategories(Long userId) {
		return expenseRepository.findByUserIdOrderByDateDesc(userId)
				.stream().map(Expense::getCategory).distinct().toList();
	}

	@Override
	public Optional<Expense> getExpenseById(long id, Long userId){
		return expenseRepository.findByIdAndUserId(id, userId)
				.stream().filter(expense -> expense.getId() == id).findFirst();
	}

	@Override
	public Expense addExpense(Expense expense , Long userId) {
		Optional<AppUser> userOptional = userService.findUserById(userId);
		if(userOptional.isPresent()){
			AppUser user = userOptional.get();
			// Ensure the incoming expense ID is null for new entities to avoid optimistic locking issues
			expense.setId(null);
			expense.setUser(user);
			return expenseRepository.save(expense);
		}else{
			throw new RuntimeException("User not found");
		}
	}

	@Override
	public boolean updateExpense(Expense updatedExpense, Long userId) {
		Optional<Expense> existingExpense =
				expenseRepository.findByIdAndUserId(updatedExpense.getId(), userId);
		if(existingExpense.isPresent()){
			updatedExpense.setUser(existingExpense.get().getUser());
			expenseRepository.save(updatedExpense);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteExpense(Long id, Long userId) {
		Optional<Expense> existingExpense = expenseRepository.findByIdAndUserId(id, userId);
		if(existingExpense.isPresent()){
			expenseRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
