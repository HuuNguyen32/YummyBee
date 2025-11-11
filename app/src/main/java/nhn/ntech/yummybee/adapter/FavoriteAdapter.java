package nhn.ntech.yummybee.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.activity.DetailFoodActivity;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<FoodItem> favoriteList;

    private MainViewModel mainViewModel;


    public FavoriteAdapter(List<FoodItem> favoriteList, MainViewModel mainViewModel) {
        this.favoriteList = favoriteList != null ? favoriteList : new ArrayList<>();
        this.mainViewModel = mainViewModel;
    }

    public void setData(List<FoodItem> favoriteList) {
        this.favoriteList = favoriteList != null ? favoriteList : new ArrayList<>();
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        FoodItem item = favoriteList.get(position);
        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice;
        ImageView imgFood;
        ConstraintLayout constraintDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgFood = itemView.findViewById(R.id.imgFood);
            constraintDelete = itemView.findViewById(R.id.constraintDelete);
        }

        @SuppressLint("DefaultLocale")
        public void bind(FoodItem item) {
            txtName.setText(item.getName());
            txtPrice.setText(String.format("%,d đ", item.getPrice()));
            Glide.with(itemView.getContext())
                    .load(item.getImageUrl())
                    .apply(new RequestOptions().transform(new CenterInside()))
                    .into(imgFood);

            constraintDelete.setOnClickListener(view -> {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    mainViewModel.toggleFavorite(item, true); // Xóa khỏi yêu thích
                    Toast.makeText(view.getContext(), "Đã xóa 1 sản phẩm khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    favoriteList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }

            });

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), DetailFoodActivity.class);
                intent.putExtra("foodItem", item);
                view.getContext().startActivity(intent);
            });
        }

    }
}
