package models;

import java.math.BigDecimal;

public class FinancialData {

    private int financialDataId;
    private BigDecimal amount;
    private String date;
    private String name;
    private String description;
    private String username;
    private String categoryName;

    public FinancialData(int financialDataId, BigDecimal amount, String date, String name, String description, String username, String categoryName) {
        this.financialDataId = financialDataId;
        this.amount = amount;
        this.date = date;
        this.name = name;
        this.description = description;
        this.username = username;
        this.categoryName = categoryName;
    }

    public int getFinancialDataId() {
        return financialDataId;
    }

    public void setFinancialDataId(int financialDataId) {
        this.financialDataId = financialDataId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
