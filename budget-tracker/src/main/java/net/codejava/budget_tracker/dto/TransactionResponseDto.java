package net.codejava.budget_tracker.dto;


import net.codejava.budget_tracker.model.TransactionType;


import java.math.BigDecimal;
import java.time.LocalDate;



public class TransactionResponseDto {  
    private String id;
    private LocalDate date;
    private TransactionType type;
    private String category;
    private String description;
    private BigDecimal amount;

    public TransactionResponseDto () {
     }

    public TransactionResponseDto(String id, LocalDate date, TransactionType type, String category, String description, BigDecimal amount) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date=date;
    }

    public TransactionType gettype() {
        return type;
    }

    public void setType (TransactionType type) {
        this.type = type;
    }

    public String getcategory(){
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    } 

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount (BigDecimal amount) {
        this.amount = amount;
    }

































}

