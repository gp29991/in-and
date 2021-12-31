package com.sm.in_and;

import java.time.LocalDate;

public class Options {
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private String viewScope;
    private String viewType;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getViewScope() {
        return viewScope;
    }

    public void setViewScope(String viewScope) {
        this.viewScope = viewScope;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
