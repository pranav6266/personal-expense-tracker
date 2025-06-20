package com.pranav.expensetrackerui.models;

import lombok.*;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Expense {
    private int id; //will have getters and setters but won't be in constructor
    private final int expenseType;  // 0 for expense, 1 for income
    private final LocalDate date;
    private final double amount;
    private final String category;
    private final String account;
    private final String note;

}
