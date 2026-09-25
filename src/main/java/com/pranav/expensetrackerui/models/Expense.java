package com.pranav.expensetrackerui.models;

import lombok.*;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Expense {
    private Integer id; // nullable id; avoid serializing 0 for new expenses
    private final int expenseType;  // 0 for expense, 1 for income
    private final LocalDate date;
    private final double amount;
    private final String category;
    private final String account;
    private final String note;

}
