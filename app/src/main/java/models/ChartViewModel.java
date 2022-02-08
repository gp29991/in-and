package models;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ChartViewModel {
    private BigDecimal totalInc;
    private BigDecimal totalExp;
    private ArrayList<FinancialDataGrouped> financialDataGrouped;

    public BigDecimal getTotalInc() {
        return totalInc;
    }

    public void setTotalInc(BigDecimal totalInc) {
        this.totalInc = totalInc;
    }

    public BigDecimal getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(BigDecimal totalExp) {
        this.totalExp = totalExp;
    }

    public ArrayList<FinancialDataGrouped> getFinancialDataGrouped() {
        return financialDataGrouped;
    }

    public void setFinancialDataGrouped(ArrayList<FinancialDataGrouped> financialDataGrouped) {
        this.financialDataGrouped = financialDataGrouped;
    }
}
