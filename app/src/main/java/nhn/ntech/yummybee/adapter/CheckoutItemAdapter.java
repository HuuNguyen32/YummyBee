package nhn.ntech.yummybee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Locale;
import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.CartItem;


public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {

    private ArrayList<CartItem> cartItems;

    public CheckoutItemAdapter(ArrayList<CartItem> cartItems) {
        this.cartItems = (cartItems != null) ? cartItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item "chỉ đọc" (viewholder_checkout_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_checkout_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Gán dữ liệu
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format(Locale.getDefault(), "%,d đ", item.getPrice()));

        // Hiển thị số lượng (ví dụ: "x2")
        holder.txtQuantity.setText("x" + item.getQuantity());

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void setData(ArrayList<CartItem> cartItems){
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtName, txtPrice, txtQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }
    }
}