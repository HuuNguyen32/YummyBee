package nhn.ntech.yummybee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.CategoryItem;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<CategoryItem> categoryItems;

    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem category);
    }

    public CategoryAdapter(ArrayList<CategoryItem> categoryItems, OnCategoryClickListener listener) {
        this.categoryItems = categoryItems != null ? categoryItems : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItems.get(position);
        Glide.with(holder.itemView.getContext())
                .load(categoryItem.getImageUrl())
                .apply(new RequestOptions().transform(new CenterInside()))
                .into(holder.imgCategory);

        holder.txtCategory.setText(categoryItem.getName());
        holder.itemView.setOnClickListener(view -> {
            if (listener != null){
                listener.onCategoryClick(categoryItem);
            }
        });
    }

    @Override
    public int getItemCount() {
       return categoryItems != null ? categoryItems.size() : 0;
    }

    public void setCategories(ArrayList<CategoryItem> newCategorys) {
        this.categoryItems = newCategorys != null ? newCategorys : new ArrayList<>();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategory;
        TextView txtCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}
