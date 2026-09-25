package com.pranav.controller;

import com.pranav.model.AppUser;
import com.pranav.model.Expense;
import com.pranav.service.ExpenseService;
import com.pranav.service.UserService;
import com.pranav.utils.ExpenseDataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

@RestController
public class ExpenseController {

	private final ExpenseService expenseService;
	private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }


	@GetMapping("expenses")
	public ResponseEntity<List<Expense>> getAllExpenses(Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		List<Expense> expenses = expenseService.getAllUserExpenses(user.getId());
		return ResponseEntity.ok(expenses);
	}

    @GetMapping("expenses/day/{date}")
	public ResponseEntity<List<Expense>> getExpensesByDay(@PathVariable("date") String date,
														  Authentication authentication){
		String username = authentication.getName();
		AppUser user =  userService.findByUsername(username);
		List<Expense> expenses = expenseService.getExpenseByDay(date, user.getId());
		return ResponseEntity.ok(expenses);
	}

	@GetMapping("/expenses/categories")
	public ResponseEntity<List<String>> getAllExpenseCategories(Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		List<String> categories = expenseService.getAllExpenseCategories(user.getId());
		if(categories.isEmpty()){
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/expenses/category/{category}/month")
	public ResponseEntity<List<Expense>> getExpensesByCategoryAndMonth(
			@PathVariable String category,
			@RequestParam String month,
			Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		List<Expense> expenses = expenseService.getExpenseByCategoryAndMonth(category, month, user.getId());
		return ResponseEntity.ok(expenses);
	}

	@GetMapping("expenses/id/{id}")
	public ResponseEntity<Optional<Expense>> getExpensesById
			(@PathVariable long id, Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		return ResponseEntity.ok(expenseService.getExpenseById(id, user.getId()));
	}

	@PostMapping("/expenses")
	public ResponseEntity<Expense> addExpense(@RequestBody Expense expense,
	Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		Expense newExpense = expenseService.addExpense(expense, user.getId());
		return new ResponseEntity<>(newExpense, HttpStatus.CREATED);
	}

	@PutMapping("/expenses/{id}")
	public ResponseEntity<Expense> updateExpense(@PathVariable Long id,
												 @RequestBody Expense expense,
													Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		expense.setId(id);
		boolean isUpdated = expenseService.updateExpense(expense,user.getId());
		if(isUpdated){
			return new ResponseEntity<>(expense, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/expenses/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable Long id,
											  Authentication authentication){
		String username = authentication.getName();
		AppUser user = userService.findByUsername(username);
		boolean isDeleted = expenseService.deleteExpense(id,user.getId());
		if(isDeleted){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
