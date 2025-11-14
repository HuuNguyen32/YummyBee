package nhn.ntech.yummybee.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class CartItem implements Serializable {

    @Exclude
    private String cartId;

    private String food_id;
    private long quantity;
    private String name;
    private long price;
    private String imageUrl;
    private transient Timestamp added_at;

    public CartItem() {
    }

    public CartItem(String food_id, long quantity, String name, long price, String imageUrl, Timestamp added_at) {
        this.food_id = food_id;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.added_at = added_at;
    }

    @Exclude
    public String getCartId() {
        return cartId;
    }

    @Exclude
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getAdded_at() {
        return added_at;
    }

    public void setAdded_at(Timestamp added_at) {
        this.added_at = added_at;
    }

}