package net.codejava.budget_tracker.dto;

import net.codejava.budget_tracker.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequestDto {
    

    @NotNull(message = "Data is required!")
    @PastOrPresent(message = "Data connot be in the future")
    private LocalDate date;

    @NotNull(message = "Type is required and must be INCOME or EXPENSE")
    private TransactionType  type;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be at most 50 characters")
    private String category;

    @NotBlank(message = "Description is Required!")
    @Size(max=200, message = "Description must be at most 200 characters")
    private String description;

    @NotNull(message = "Amount is required!")
    @DecimalMin(valid= "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 12, fraction = 2, message = "Amount may have at most 2 decimal places!")
    private BigDecimal amount;
    

    public TransactionRequestDto(){
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory (String category) {
        this.category = category;
    }



































































































}