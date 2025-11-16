package nhn.ntech.yummybee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.NotificationItem;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notificationItems;

    public NotificationAdapter(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notificationItems.get(position);

        holder.txtTitle.setText(item.getTitle());
        holder.txtMessage.setText(item.getMessage());

        // Dùng phương thức tiện ích để hiển thị thời gian
        holder.txtTime.setText(item.getFormattedTime());

        // Thiết lập Icon dựa trên loại thông báo (Dùng logic phân loại đơn giản)
        if (item.getTitle().contains("thành công")) {
            holder.imgIcon.setImageResource(R.drawable.baseline_check_circle_24);
        } else if (item.getTitle().contains("Khuyến mãi")) {
            holder.imgIcon.setImageResource(R.drawable.baseline_local_offer_24);
        } else {
            holder.imgIcon.setImageResource(R.drawable.baseline_notifications_24);
        }
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public void setNotifications(List<NotificationItem> newItems) {
        this.notificationItems = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtMessage, txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}