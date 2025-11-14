package nhn.ntech.yummybee.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nhn.ntech.yummybee.model.AddressItem;
import nhn.ntech.yummybee.model.CartItem;
import nhn.ntech.yummybee.model.CategoryItem;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.model.OrderItem;
import nhn.ntech.yummybee.model.UserItem;

public class MainRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public LiveData<ArrayList<CategoryItem>> fetchCategories(){
        MutableLiveData<ArrayList<CategoryItem>> listCategories = new MutableLiveData<>();

        firestore.collection("categories")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<CategoryItem> categoryItems = new ArrayList<>();

                    if (!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot ds : documents) {
                            // 1. Ánh xạ Document sang POJO
                            CategoryItem category = ds.toObject(CategoryItem.class);

                            // 2. Lấy Document ID (GA_RAN, BURGER) và gán vào POJO
                            if (category != null) {
                                category.setId(ds.getId());
                                categoryItems.add(category);
                            }
                        }
                        listCategories.setValue(categoryItems);
                    }
                    else {
                        listCategories.setValue(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    listCategories.setValue(new ArrayList<>());
                });

        return listCategories;
    }

    public LiveData<ArrayList<FoodItem>> fetchFoods() {
        MutableLiveData<ArrayList<FoodItem>> listFoods = new MutableLiveData<>();
        firestore.collection("food_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<FoodItem> foodItems = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : documentSnapshots) {
                            FoodItem foodItem = ds.toObject(FoodItem.class);
                            if (foodItem != null) {
                                foodItem.setId(ds.getId());
                                foodItems.add(foodItem);
                            }
                        }
                        listFoods.setValue(foodItems);
                    } else {
                        listFoods.setValue(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    listFoods.setValue(new ArrayList<>());
                });

        return listFoods;
    }

    public LiveData<ArrayList<FoodItem>> fetchLimitedFoods() {
        MutableLiveData<ArrayList<FoodItem>> listFoods = new MutableLiveData<>();

        String drinkCategoryId = "DO_UONG";

        firestore.collection("food_items")
                .whereNotEqualTo("category_id", drinkCategoryId)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<FoodItem> foodItems = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : documentSnapshots) {
                            FoodItem foodItem = ds.toObject(FoodItem.class);
                            if (foodItem != null) {
                                foodItem.setId(ds.getId());
                                foodItems.add(foodItem);
                            }
                        }
                        listFoods.setValue(foodItems);
                    } else {
                        listFoods.setValue(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    listFoods.setValue(new ArrayList<>());
                });

        return listFoods;
    }

    public LiveData<ArrayList<FoodItem>> fetchFoodsByCategoryId(String categoryId){
        MutableLiveData<ArrayList<FoodItem>> listFoods = new MutableLiveData<>();

        // Kiểm tra ID hợp lệ (Nên thêm kiểm tra null/empty ở đây nếu chưa có)
        if (categoryId == null || categoryId.isEmpty()) {
            listFoods.setValue(new ArrayList<>());
            return listFoods;
        }

        firestore.collection("food_items")
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<FoodItem> foodItems = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : documentSnapshots){
                            FoodItem foodItem = ds.toObject(FoodItem.class);
                            if (foodItem != null){
                                foodItem.setId(ds.getId());
                                foodItems.add(foodItem);
                            }
                        }
                        listFoods.setValue(foodItems);
                    }
                    else {
                        listFoods.setValue(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> listFoods.setValue(new ArrayList<>()));
        return listFoods;
    }

    public LiveData<CategoryItem> fetchCategoryInfo(String categoryId) {
        MutableLiveData<CategoryItem> category = new MutableLiveData<>();

        if (categoryId == null || categoryId.isEmpty()) {
            category.setValue(null); // Trả về null nếu ID rỗng
            return category;
        }

        firestore.collection("categories").document(categoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        CategoryItem item = documentSnapshot.toObject(CategoryItem.class);
                        if (item != null) {
                            item.setId(documentSnapshot.getId());
                            category.setValue(item);
                        }
                    } else {
                        category.setValue(null); // Không tìm thấy
                    }
                })
                .addOnFailureListener(e -> category.setValue(null)); // Lỗi

        return category;
    }

    public LiveData<UserItem> fetchUserByEmail(String email) {
        MutableLiveData<UserItem> userLiveData = new MutableLiveData<>();

        if (email == null || email.isEmpty()) {
            userLiveData.setValue(null);
            return userLiveData;
        }

       firestore.collection("users")
                .whereEqualTo("email", email) // Tìm Document có trường "email" khớp
                .limit(1) // Chỉ lấy 1 kết quả (vì email là duy nhất)
               .get()
               .addOnSuccessListener(queryDocumentSnapshots -> {
                   if (!queryDocumentSnapshots.isEmpty()) {
                       // Lấy Document đầu tiên tìm thấy
                       DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                       UserItem user = document.toObject(UserItem.class);

                       if (user != null) {
                           user.setUid(document.getId()); // Gán UID (Document ID)
                           userLiveData.setValue(user);
                       }
                   } else {
                       userLiveData.setValue(null);
                   }
               }).addOnFailureListener(e -> {
                   userLiveData.setValue(null);
               });

        return userLiveData;
    }

    public LiveData<ArrayList<FoodItem>> fetchFavoriteFoods(String userId) {
        MutableLiveData<ArrayList<FoodItem>> favoriteFoods = new MutableLiveData<>();

        if (userId == null || userId.isEmpty()) {
            favoriteFoods.setValue(new ArrayList<>());
            return favoriteFoods;
        }

        firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<FoodItem> foodItems = new ArrayList<>();
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                        FoodItem foodItem = ds.toObject(FoodItem.class);
                        if (foodItem != null) {
                            foodItem.setId(ds.getId());
                            foodItems.add(foodItem);
                        }
                    }
                    favoriteFoods.setValue(foodItems);
                })
                .addOnFailureListener(e -> favoriteFoods.setValue(new ArrayList<>()));

        return favoriteFoods;
    }

    /**
     * Thêm/Xóa một món khỏi danh sách Yêu thích (Logic Toggle).
     */
    public void toggleFavorite(String userId, FoodItem foodItem, boolean isCurrentlyFavorite) {
        // ID của món ăn sẽ là ID của Document
        if (userId == null || foodItem == null || foodItem.getId() == null) {
            Log.e("toggleFavorite", "userId hoặc foodId bị null");
            return;
        }

        String foodId = foodItem.getId();

        DocumentReference favoriteRef = firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(foodId);

        if (isCurrentlyFavorite) {
            // HÀNH ĐỘNG 1: Xóa (Nếu đang yêu thích)
            favoriteRef.delete();
        } else {
            // HÀNH ĐỘNG 2: Thêm (Nếu chưa yêu thích)
            Map<String, Object> data = new HashMap<>();
            data.put("added_at", FieldValue.serverTimestamp());
            data.put("name", foodItem.getName());
            data.put("price", foodItem.getPrice());
            data.put("imageUrl", foodItem.getImageUrl());

            favoriteRef.set(data);
        }
    }

    public LiveData<Boolean> isFavorite(String userId, String foodId) {
        MutableLiveData<Boolean> isFavoriteLiveData = new MutableLiveData<>();
        if (userId == null || foodId == null) {
            isFavoriteLiveData.setValue(false);
            return isFavoriteLiveData;
        }
        firestore.collection("users").document(userId)
                .collection("favorites").document(foodId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        isFavoriteLiveData.setValue(false);
                        return;
                    }
                    isFavoriteLiveData.setValue(documentSnapshot != null && documentSnapshot.exists());
                });
        return isFavoriteLiveData;
    }

    public LiveData<ArrayList<CartItem>> fetchCartItems(String userId) {
        MutableLiveData<ArrayList<CartItem>> cartLiveData = new MutableLiveData<>();

        firestore.collection("users").document(userId)
                .collection("cart")
                .orderBy("added_at", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        cartLiveData.setValue(new ArrayList<>());
                        return; // Lỗi
                    }

                    ArrayList<CartItem> cartItems = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {

                            // Sử dụng CartItem.class để ánh xạ
                            CartItem item = ds.toObject(CartItem.class);

                            if (item != null) {
                                // Gán ID của Document (Auto-ID) vào POJO
                                item.setCartId(ds.getId());
                                cartItems.add(item);
                            }
                        }
                    }
                    cartLiveData.setValue(cartItems);
                });

        return cartLiveData;
    }

    public void addToCart(String userId, FoodItem foodItem, long quantity) {
        if (foodItem == null || foodItem.getId() == null) {
            return; // Không thể thêm món ăn không có ID
        }

        // 1. Tìm xem món này đã có trong giỏ hàng chưa (dựa trên food_id)
        Query query = firestore.collection("users").document(userId).collection("cart")
                .whereEqualTo("food_id", foodItem.getId())
                .limit(1);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                // TRƯỜNG HỢP 1: Món này đã có trong giỏ hàng -> Tăng số lượng
                DocumentSnapshot cartDoc = queryDocumentSnapshots.getDocuments().get(0);
                long currentQuantity = 0;

                if (cartDoc.getLong("quantity") != null) {
                    currentQuantity = cartDoc.getLong("quantity");
                }

                cartDoc.getReference().update("quantity", currentQuantity + quantity);

            } else {
                // TRƯỜG HỢP 2: Món mới -> Thêm vào giỏ hàng
                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("food_id", foodItem.getId());
                cartItem.put("quantity", quantity);
                cartItem.put("name", foodItem.getName());
                cartItem.put("price", foodItem.getPrice());
                cartItem.put("imageUrl", foodItem.getImageUrl());
                cartItem.put("added_at", FieldValue.serverTimestamp());

                firestore.collection("users")
                        .document(userId)
                        .collection("cart")
                        .add(cartItem);
            }
        });
    }

    public void removeCartItem(String userId, String cartItemId) {
        if (userId == null || cartItemId == null) return;

        firestore.collection("users").document(userId)
                .collection("cart").document(cartItemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // (Tùy chọn: log thành công)
                })
                .addOnFailureListener(e -> {
                    // (Tùy chọn: log lỗi)
                });
    }

    public void clearCart(String userId) {
        if (userId == null) return;

        firestore.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) return; // Không có gì để xóa

                    // 1. Tạo một WriteBatch
                    WriteBatch batch = firestore.batch();

                    // 2. Lặp qua tất cả document và thêm lệnh xóa vào batch
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                        batch.delete(ds.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        // (Tùy chọn: log xóa thành công)
                    });
                });
    }

    public void updateCartQuantity(String userId, String cartItemId, long newQuantity) {
        if (userId == null || cartItemId == null || newQuantity <= 0) return;

        DocumentReference cartRef = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(cartItemId);

        cartRef.update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Cart", "Đã cập nhật số lượng thành công");
                })
                .addOnFailureListener(e -> {
                    Log.e("Cart", "Lỗi khi cập nhật số lượng", e);
                });
    }


    public void updateUserProfile(String userId, Map<String, Object> updates, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (userId == null || updates == null || updates.isEmpty()) {
            onFailure.onFailure(new Exception("Dữ liệu cập nhật hoặc UserID không hợp lệ"));
            return;
        }

        firestore.collection("users").document(userId)
                .update(updates) // Chỉ cập nhật các trường trong Map
                .addOnSuccessListener(onSuccess) // Trả về thành công
                .addOnFailureListener(onFailure); // Trả về thất bại
    }

    public LiveData<ArrayList<AddressItem>> fetchAddresses(String userId) {
        MutableLiveData<ArrayList<AddressItem>> addressesLiveData = new MutableLiveData<>();

        firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .orderBy("isDefault", Query.Direction.DESCENDING) // Mặc định (true) lên đầu
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        addressesLiveData.setValue(new ArrayList<>());
                        return; // Lỗi
                    }

                    ArrayList<AddressItem> addressList = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            AddressItem item = ds.toObject(AddressItem.class);
                            if (item != null) {
                                item.setId(ds.getId()); // Gán ID Document
                                addressList.add(item);
                            }
                        }
                    }
                    addressesLiveData.setValue(addressList);
                });

        return addressesLiveData;
    }

    public Task<DocumentReference> addNewAddress(String userId, AddressItem address) {

        // Thêm đối tượng 'address' vào sub-collection 'addresses'
        return firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .add(address);
    }

    public Task<Void> updateAddress(String userId, String addressId, Map<String, Object> updates) {
        if (userId == null || addressId == null || updates == null || updates.isEmpty()) {
            // Trả về một Task thất bại ngay lập tức nếu thiếu thông tin
            return Tasks.forException(new IllegalArgumentException("UserID, AddressID, hoặc dữ liệu cập nhật bị rỗng"));
        }

        // Dùng .update() để cập nhật các trường trong Map
        return firestore.collection("users").document(userId)
                .collection("addresses").document(addressId)
                .update(updates);
    }


    public Task<String> placeOrder(String userId, ArrayList<CartItem> cartItems,
                                   long totalAmount, String paymethod, AddressItem address) {

        if (userId == null || cartItems == null || cartItems.isEmpty() || address == null) {
            return Tasks.forException(new IllegalArgumentException("Dữ liệu đặt hàng không hợp lệ"));
        }

        // 1. Chuẩn bị danh sách món ăn (Array of Maps)
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("food_id", item.getFood_id());
            map.put("name", item.getName());
            map.put("price", item.getPrice());
            map.put("quantity", item.getQuantity());
            map.put("imageUrl", item.getImageUrl());
            itemList.add(map);
        }

        // 2. Tạo dữ liệu đơn hàng
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user_id", userId);
        String fullAddress = address.getFullName() + ", " +
                address.getStreet() + ", " +
                address.getWard() + ", " +
                address.getDistrict() + ", " +
                address.getCity();
        orderData.put("address", fullAddress);
        orderData.put("paymentMethod", paymethod);
        orderData.put("total_amount", totalAmount);
        orderData.put("status", "Completed");
        orderData.put("items", itemList);
        orderData.put("order_time", FieldValue.serverTimestamp());

        // 3. Lưu vào Firestore (sub-collection 'orders' của user)
        return firestore.collection("users")
                .document(userId)
                .collection("orders")
                .add(orderData)
                .onSuccessTask(documentReference -> {
                    clearCart(userId);
                    return Tasks.forResult(documentReference.getId());
                });
    }

    public LiveData<ArrayList<OrderItem>> fetchOrderHistory(String userId) {
        MutableLiveData<ArrayList<OrderItem>> ordersLiveData = new MutableLiveData<>();

        if (userId == null) {
            ordersLiveData.setValue(new ArrayList<>());
            return ordersLiveData;
        }

        firestore.collection("users").document(userId)
                .collection("orders")
                .orderBy("order_time", Query.Direction.DESCENDING)

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<OrderItem> orderList = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            OrderItem item = ds.toObject(OrderItem.class);
                            if (item != null) {
                                item.setOrderId(ds.getId());
                                orderList.add(item);
                            }
                        }
                    }
                    ordersLiveData.setValue(orderList);
                })
                .addOnFailureListener(e -> {
                    // Nếu lỗi, trả về danh sách rỗng
                    ordersLiveData.setValue(new ArrayList<>());
                });

        return ordersLiveData;
    }

    public LiveData<OrderItem> fetchOrderDetails(String orderId, String userId) {
        MutableLiveData<OrderItem> orderLiveData = new MutableLiveData<>();

        if (orderId == null || orderId.isEmpty()) {
            orderLiveData.setValue(null);
            return orderLiveData;
        }

        firestore.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        OrderItem item = documentSnapshot.toObject(OrderItem.class);
                        if (item != null) {
                            item.setOrderId(documentSnapshot.getId());
                            orderLiveData.setValue(item);
                        }
                    } else {
                        orderLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> orderLiveData.setValue(null));

        return orderLiveData;
    }

    public Task<Void> deleteSingleOrder(String userId, String orderId) {
        if (userId == null || orderId == null) {
            return Tasks.forException(new IllegalArgumentException("UserID hoặc OrderID không hợp lệ"));
        }

        return firestore.collection("users").document(userId)
                .collection("orders").document(orderId)
                .delete();
    }
}