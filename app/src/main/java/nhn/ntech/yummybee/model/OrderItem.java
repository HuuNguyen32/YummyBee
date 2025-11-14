package nhn.ntech.yummybee.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OrderItem implements Serializable {

    @Exclude
    private String orderId;

    private String user_id;
    private long total_amount;
    private String address;
    private String status;
    private Timestamp order_time;
    private String paymentMethod;
    private List<Map<String, Object>> items;


    public OrderItem() {
    }

    public OrderItem(String user_id, long total_amount, String address, String status, Timestamp order_time) {
        this.user_id = user_id;
        this.total_amount = total_amount;
        this.address = address;
        this.status = status;
        this.order_time = order_time;
    }

    @Exclude
    public String getOrderId() {
        return orderId;
    }

    @Exclude
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(long total_amount) {
        this.total_amount = total_amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Timestamp order_time) {
        this.order_time = order_time;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
