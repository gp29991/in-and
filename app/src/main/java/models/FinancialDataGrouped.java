package models;

import java.math.BigDecimal;

public class FinancialDataGrouped {
    private BigDecimal amount;
    private String categoryName;

    public FinancialDataGrouped(BigDecimal amount, String categoryName) {
        this.amount = amount;
        this.categoryName = categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
