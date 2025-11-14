package nhn.ntech.yummybee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Locale;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.activity.DetailFoodActivity;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;
import nhn.ntech.yummybee.viewmodel.SharedViewModel;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private ArrayList<FoodItem> foods;
    private final OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onAddToCartClick(FoodItem foodItem); // Click nút Add
    }

    public FoodAdapter(ArrayList<FoodItem> foods, OnFoodClickListener listener) {
        this.foods = foods != null ? foods : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_food, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder holder, int position) {
        FoodItem foodItem = foods.get(position);

        holder.txtTitleFood.setText(foodItem.getName());
        holder.txtPriceFood.setText(String.format(Locale.getDefault(), "%,d đ", foodItem.getPrice()));

        Context itemContext = holder.itemView.getContext();

        Glide.with(itemContext)
                .load(foodItem.getImageUrl())
                .apply(new RequestOptions().transform(new CenterInside()))
                .into(holder.imgFood);

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onAddToCartClick(foodItem);
                }
            }
        });

        holder.itemView.setOnClickListener(view -> {
            // chuyển sang màn hình chi tiết món ăn (DetailActivity)
            Intent intent = new Intent(view.getContext(), DetailFoodActivity.class);
            intent.putExtra("foodItem", foodItem);
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return foods != null ? foods.size() : 0;
    }


    public void setFoods(ArrayList<FoodItem> newFoods) {
        this.foods = newFoods != null ? newFoods : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ViewHolder Class (giữ nguyên)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView, addToCart;
        ImageView imgFood;
        TextView txtTitleFood, txtPriceFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardFood);
            addToCart = itemView.findViewById(R.id.addToCart);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtTitleFood = itemView.findViewById(R.id.txtTitleFood);
            txtPriceFood = itemView.findViewById(R.id.txtPriceFood);
        }
    }
}