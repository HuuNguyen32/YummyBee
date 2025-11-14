package nhn.ntech.yummybee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.OrderItem;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<OrderItem> orders;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderItem order);
        void onDeleteClick(OrderItem order);
    }

    public OrderHistoryAdapter(ArrayList<OrderItem> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public OrderItem getItem(int position) {
        return orders.get(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order_history_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orders.get(position);

        holder.txtOrderId.setText("Đơn hàng #" + item.getOrderId().substring(0, 8).toUpperCase());
        holder.txtOrderStatus.setText(item.getStatus());
        holder.txtOrderTotal.setText("Tổng tiền: " +
                String.format(Locale.getDefault(), "%,d đ", item.getTotal_amount()));

        if (item.getOrder_time() != null) {
            Date date = item.getOrder_time().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.txtOrderDate.setText("Ngày đặt: " + sdf.format(date));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(item);
            }
        });

        holder.constraintDeleteOrderHistory.setOnClickListener(view -> {
            if (listener != null){
                listener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(ArrayList<OrderItem> newOrders) {
        this.orders = newOrders != null ? newOrders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderStatus, txtOrderDate, txtOrderTotal;
        ConstraintLayout constraintDeleteOrderHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtOrderTotal = itemView.findViewById(R.id.txtOrderTotal);
            constraintDeleteOrderHistory = itemView.findViewById(R.id.constraintDeleteOrderHistory);
        }
    }
}