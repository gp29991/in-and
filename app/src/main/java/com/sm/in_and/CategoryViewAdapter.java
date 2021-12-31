package com.sm.in_and;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import helpers.LocalizationHelper;
import models.FinancialData;
import models.FinancialDataGrouped;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder> {
    private ArrayList<FinancialDataGrouped> data = new ArrayList<>();

    @NonNull
    @Override
    public CategoryViewAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view_item, parent, false);
        return new CategoryViewAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewAdapter.CategoryViewHolder holder, int position) {
        FinancialDataGrouped item = data.get(position);
        holder.tvAmount.setText(String.format(new Locale("pl","PL"), "%.2f", item.getAmount()));
        if(item.getAmount().compareTo(new BigDecimal(0)) < 0){
            holder.tvAmount.setTextColor(Color.parseColor("#E91E63"));
        }else{
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }
        holder.tvCategory.setText(item.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAmount;
        public TextView tvCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }

    public void setData(ArrayList<FinancialDataGrouped> data) {
        this.data = data;
    }

    public void updateData(ArrayList<FinancialDataGrouped> data){
        this.data = data;
        this.notifyDataSetChanged();
    }
}
