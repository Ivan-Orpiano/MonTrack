package net.codejava.budget_tracker.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.codejava.budget_tracker.dto.TransactionResponseDto;
import net.codejava.budget_tracker.model.TransactionType;
import net.codejava.budget_tracker.service.TransactionService;

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



    /** GET /api/transactions/{id} */    
    @GetMapping("/{id}")
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactionById(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }


    /**POST api/transactions */
    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        TransactionReponseDto created = transactionService.createTransaction(dto);
        return ResponseEntity.created(URI.create("/api/tranactions/" + created.getId())).body(created);
    }

        
}
