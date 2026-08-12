package net.codejava.budget_tracker.dto;

import java.math.BigDecimal;

public class TransactionSummaryDto {
    
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private int transactionCount;

    public TransactionSummaryDto(){
    }

    public TransactionSummaryDto(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance, int transactionCount){
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.transactionCount = transactionCount;
    }




}
