package com.sm.in_and;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import models.Category;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListHolder> {
    private ArrayList<Category> categories = new ArrayList<>();
    private CategoryListAdapterListener listener;

    @NonNull
    @Override
    public CategoryListAdapter.CategoryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new CategoryListAdapter.CategoryListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.CategoryListHolder holder, int position) {
        Category item = categories.get(position);
        holder.tvCategoryName.setText(item.getCategoryName());
        if(item.getCategoryId() == 0){
            holder.ibEditCategory.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryListHolder extends RecyclerView.ViewHolder{
        public TextView tvCategoryName;
        public ImageButton ibEditCategory;
        public ImageButton ibDeleteCategory;

        public CategoryListHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            ibEditCategory = itemView.findViewById(R.id.ibEditCategory);
            ibDeleteCategory = itemView.findViewById(R.id.ibDeleteCategory);

            ibEditCategory.setOnClickListener(new View.OnClickListener() {
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

            ibDeleteCategory.setOnClickListener(new View.OnClickListener() {
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

    public void setData(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void setCategoryListAdapterListener(CategoryListAdapterListener listener){
        this.listener = listener;
    }

    public interface CategoryListAdapterListener{
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}
