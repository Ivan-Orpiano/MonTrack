package net.codejava.budget_tracker.controller;


import net.codejava.budget_tracker.dto.TransactionRequestDto;
import net.codejava.budget_tracker.dto.TransactionResponseDto;
import net.codejava.budget_tracker.dto.TransactionSummaryDto;
import net.codejava.budget_tracker.model.TransactionType;
import net.codejava.budget_tracker.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;



//REST API Controller 

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Get API 

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions(
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            return ResponseEntity.ok(transactionService.getTransactions(type, category, startDate, endDate));
        }     
}
