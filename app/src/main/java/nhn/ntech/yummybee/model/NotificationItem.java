package nhn.ntech.yummybee.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationItem implements Serializable {

    private String title;
    private String message;
    private Timestamp time; // Kiểu Timestamp của Firebase
    private boolean isRead = false;

    private transient int iconResId;

    public NotificationItem() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean read) { isRead = read; }

    public Timestamp getTime() { return time; }
    public void setTime(Timestamp time) { this.time = time; }

    // Chỉ dùng trên Client
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public String getFormattedTime() {
        if (time == null) return "Vừa xong";
        try {
            Date date = time.toDate();
            // Định dạng thời gian: Ví dụ: 10:30 - 20/12
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            return "Vừa xong";
        }
    }
}