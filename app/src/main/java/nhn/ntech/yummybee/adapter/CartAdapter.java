package nhn.ntech.yummybee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.CartItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private MainViewModel mainViewModel;

    public CartAdapter(List<CartItem> cartItems, MainViewModel mainViewModel) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        this.mainViewModel = mainViewModel;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void setData(List<CartItem> items) {
        this.cartItems = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQuantity, txtMinus, txtPlus;
        ImageView imgFood;
        View btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtMinus = itemView.findViewById(R.id.txtMinus);
            txtPlus = itemView.findViewById(R.id.txtPlus);
            imgFood = itemView.findViewById(R.id.imgFood);
            btnDelete = itemView.findViewById(R.id.constraintDelete);
        }

        public void bind(CartItem item) {
            txtName.setText(item.getName());
            txtPrice.setText(String.format("%,d đ", item.getPrice()));
            txtQuantity.setText(String.valueOf(item.getQuantity()));
            Glide.with(itemView.getContext()).load(item.getImageUrl()).into(imgFood);

            txtPlus.setOnClickListener(v -> {
                long qty = item.getQuantity() + 1;
                item.setQuantity(qty);
                txtQuantity.setText(String.valueOf(qty));
                mainViewModel.updateCartQuantity(item.getCartId(), qty);
            });

            txtMinus.setOnClickListener(v -> {
                long qty = item.getQuantity();
                if (qty > 1) {
                    qty--;
                    item.setQuantity(qty);
                    txtQuantity.setText(String.valueOf(qty));
                    mainViewModel.updateCartQuantity(item.getCartId(), qty);
                } else {
                    mainViewModel.removeFromCart(item.getCartId());
                    Toast.makeText(itemView.getContext(), "Đã xóa 1 sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            });

            btnDelete.setOnClickListener(v -> {
                mainViewModel.removeFromCart(item.getCartId());
                Toast.makeText(itemView.getContext(), "Đã xóa 1 sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            });
        }

    }
}
