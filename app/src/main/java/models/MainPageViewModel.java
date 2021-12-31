package models;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainPageViewModel {

    private ArrayList<FinancialData> upcomingSignificantExpenses;
    private BigDecimal totalInc;
    private BigDecimal totalExp;

    public ArrayList<FinancialData> getUpcomingSignificantExpenses() {
        return upcomingSignificantExpenses;
    }

    public void setUpcomingSignificantExpenses(ArrayList<FinancialData> upcomingSignificantExpenses) {
        this.upcomingSignificantExpenses = upcomingSignificantExpenses;
    }

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
}
