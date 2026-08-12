package net.codejava.budget_tracker.service;

import net.codejava.budget_tracker.dto.TransactionRequestDto;
import net.codejava.budget_tracker.dto.TransactionResponseDto;
import net.codejava.budget_tracker.dto.TransactionSummaryDto;
import net.codejava.budget_tracker.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    

    List<TransactionResponseDto> getTransactions(TransactionType type, String category, LocalDate startDate, LocalDate endDate);


    TransactionResponseDto getTransactionById(String id);
    TransactionResponseDto createTransaction(TransactionRequestDto dto);
    TransactionResponseDto updateTransaction(String id, TransactionRequestDto dto);

    void deleteTransaction(String id);

    TransactionSummaryDto getSummary(TransactionType type, String ategory, LocalDate startDate, LocalDate endDate);
}














