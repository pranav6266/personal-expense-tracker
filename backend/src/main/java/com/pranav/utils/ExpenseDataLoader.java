package com.pranav.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranav.model.Expense;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExpenseDataLoader {
	@Getter
	private static List<Expense> expenses = new ArrayList<>();

	@PostConstruct
	public void init() {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream is = getClass().getResourceAsStream("/expenses.json");
		try {
			expenses = objectMapper.readValue(is, new TypeReference<List<Expense>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
