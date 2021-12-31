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

public class DetailViewAdapter extends RecyclerView.Adapter<DetailViewAdapter.DetailViewHolder> {
    private ArrayList<FinancialData> data = new ArrayList<>();
    private DetailViewAdapterListener listener;

    @NonNull
    @Override
    public DetailViewAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_view_item, parent, false);
        return new DetailViewAdapter.DetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewAdapter.DetailViewHolder holder, int position) {
        FinancialData item = data.get(position);
        holder.tvAmount.setText(String.format(new Locale("pl","PL"), "%.2f", item.getAmount()));
        if(item.getAmount().compareTo(new BigDecimal(0)) < 0){
            holder.tvAmount.setTextColor(Color.parseColor("#E91E63"));
        }else{
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }
        holder.tvName.setText(item.getName());
        holder.tvDate.setText(LocalizationHelper.getLocalizedDate(item.getDate()));
        holder.tvCategory.setText(item.getCategoryName());
        if(item.getDescription() == null) {
            holder.tvDescription.setText("(Brak opisu)");
        }else{
            holder.tvDescription.setText(item.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAmount;
        public TextView tvName;
        public TextView tvDate;
        public TextView tvCategory;
        public TextView tvDescription;
        public ImageButton ibExpand;
        public ConstraintLayout clExpandable;
        public CardView cvItem;
        public ImageButton ibEdit;
        public ImageButton ibDelete;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ibExpand = itemView.findViewById(R.id.ibExpand);
            clExpandable = itemView.findViewById(R.id.clExpandable);
            cvItem = itemView.findViewById(R.id.cvItem);
            ibEdit = itemView.findViewById(R.id.ibEdit);
            ibDelete = itemView.findViewById(R.id.ibDelete);

            ibExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clExpandable.getVisibility() == View.VISIBLE) {
                        //TransitionManager.beginDelayedTransition(cvItem, new AutoTransition());
                        clExpandable.setVisibility(View.GONE);
                        ibExpand.setImageResource(R.drawable.ic_baseline_expand_more_24);
                    }
                    else {
                        //TransitionManager.beginDelayedTransition(cvItem, new AutoTransition());
                        clExpandable.setVisibility(View.VISIBLE);
                        ibExpand.setImageResource(R.drawable.ic_baseline_expand_less_24);
                    }
                }
            });

            ibEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onEditClick(position);
                        }
                    }
                }
            });

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public void setData(ArrayList<FinancialData> data) {
        this.data = data;
    }

    public void updateData(ArrayList<FinancialData> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    public void setDetailViewAdapterListener(DetailViewAdapterListener listener){
        this.listener = listener;
    }

    public interface DetailViewAdapterListener{
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}
