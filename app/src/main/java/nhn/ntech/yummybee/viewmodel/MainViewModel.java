package nhn.ntech.yummybee.viewmodel;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nhn.ntech.yummybee.manager.UserSessionManager;
import nhn.ntech.yummybee.model.AddressItem;
import nhn.ntech.yummybee.model.CartItem;
import nhn.ntech.yummybee.model.CategoryItem;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.model.NotificationItem;
import nhn.ntech.yummybee.model.OrderItem;
import nhn.ntech.yummybee.model.UserItem;
import nhn.ntech.yummybee.repository.MainRepository;

public class MainViewModel extends ViewModel {

    private final MainRepository repository = new MainRepository();

    // LiveData nội bộ để lưu cache kết quả
    private final LiveData<ArrayList<CategoryItem>> cachedCategories = repository.fetchCategories();
    private final LiveData<ArrayList<FoodItem>> allFoods = repository.fetchFoods();
    private final LiveData<ArrayList<FoodItem>> limitedFoods = repository.fetchLimitedFoods();
    private final MutableLiveData<Boolean> _profileUpdateStatus = new MutableLiveData<>();
    public LiveData<Boolean> getProfileUpdateStatus() {
        return _profileUpdateStatus;
    }

    private final MutableLiveData<Boolean> _addressSaveStatus = new MutableLiveData<>();
    public LiveData<Boolean> getAddressSaveStatus() {
        return _addressSaveStatus;
    }

    private final MutableLiveData<String> _orderStatus = new MutableLiveData<>();

    public LiveData<String> getOrderStatus() {
        return _orderStatus;
    }

    private final MutableLiveData<Boolean> _singleOrderDeleteStatus = new MutableLiveData<>();
    public LiveData<Boolean> getSingleOrderDeleteStatus() {
        return _singleOrderDeleteStatus;
    }

    private final MutableLiveData<Boolean> _historyClearStatus = new MutableLiveData<>();

    public LiveData<Boolean> getHistoryClearStatus() {
        return _historyClearStatus;
    }

    private final MutableLiveData<Boolean> _favouriteClearStatus = new MutableLiveData<>();

    public LiveData<Boolean> getFavouriteClearStatus() {
        return _favouriteClearStatus;
    }

    public final MutableLiveData<Boolean> _notificationStatus = new MutableLiveData<>();

    public LiveData<Boolean> getNotificationStatus() {
        return _notificationStatus;
    }

    private final MutableLiveData<Boolean> _notificationClearStatus = new MutableLiveData<>();
    public LiveData<Boolean> getNotificationClearStatus() {
        return _notificationClearStatus;
    }

    private final MutableLiveData<Boolean> _addressDeleteStatus = new MutableLiveData<>();
    public LiveData<Boolean> getAddressDeleteStatus() {
        return _addressDeleteStatus;
    }

    public void clearFavourite() {
        String userId = getUserId();
        if (userId != null){
            repository.clearFavourite(userId)
                    .addOnSuccessListener(unused -> {
                        _favouriteClearStatus.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        _favouriteClearStatus.setValue(false);
                    });
        }else{
            _favouriteClearStatus.setValue(false);
        }

    }

    public void deleteSingleOrder(String orderId) {
        String userId = getUserId();
        if (userId == null) {
            _singleOrderDeleteStatus.setValue(false);
            return;
        }

        repository.deleteSingleOrder(userId, orderId)
                .addOnSuccessListener(aVoid -> {
                    _singleOrderDeleteStatus.setValue(true); // Xóa thành công
                })
                .addOnFailureListener(e -> {
                    _singleOrderDeleteStatus.setValue(false); // Xóa thất bại
                });
    }

    public void resetSingleOrderDeleteStatus() {
        _singleOrderDeleteStatus.setValue(null);
    }

    public void saveNewAddress(AddressItem address) {
        String userId = getUserId();

        if (userId != null) {
            repository.addNewAddress(userId, address)
                    .addOnSuccessListener(documentReference -> {
                        // Task thành công
                        _addressSaveStatus.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        // Task thất bại
                        _addressSaveStatus.setValue(false);
                    });
        }else{
            _addressSaveStatus.setValue(false); // Thất bại (chưa đăng nhập)
            return;
        }

    }

    public void clearOrderHistory() {
        String userId = getUserId();
        if (userId != null) {
            repository.clearOrderHistory(userId)
                    .addOnSuccessListener(aVoid -> _historyClearStatus.setValue(true))
                    .addOnFailureListener(e -> _historyClearStatus.setValue(false));
        } else {
            _historyClearStatus.setValue(false);
        }
    }

    public LiveData<ArrayList<OrderItem>> loadOrderHistory() {
        String userId = getUserId();
        if (userId == null) {
            return new MutableLiveData<>(new ArrayList<>());
        }
        return repository.fetchOrderHistory(userId);
    }

    public LiveData<OrderItem> loadOrderDetails(String orderId) {
         String userId = getUserId();
         return repository.fetchOrderDetails(orderId, userId);
    }

    public void placeOrder(ArrayList<CartItem> cartItems, AddressItem address,
                           long totalAmount, String paymentMethod) {

        String userId = getUserId();
        if (userId == null) {
            _orderStatus.setValue(null);
            return;
        }
        repository.placeOrder(userId, cartItems, totalAmount, paymentMethod, address)
                .addOnSuccessListener(_orderStatus::setValue)
                .addOnFailureListener(e -> {
                    _orderStatus.setValue(null);
                });
    }

    public void updateAddress(String addressId, AddressItem addressData) {
        String userId = getUserId();
        if (userId == null) {
            _addressSaveStatus.setValue(false); // Thất bại (chưa đăng nhập)
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", addressData.getFullName());
        updates.put("phone", addressData.getPhone());
        updates.put("street", addressData.getStreet());
        updates.put("ward", addressData.getWard());
        updates.put("district", addressData.getDistrict());
        updates.put("city", addressData.getCity());
        updates.put("isDefault", addressData.isDefault()); // Cập nhật trạng thái mặc định


        repository.updateAddress(userId, addressId, updates)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    _addressSaveStatus.setValue(true);
                })
                .addOnFailureListener(e -> {
                    // Cập nhật thất bại
                    _addressSaveStatus.setValue(false);
                });
    }

    public void deleteAddress(String addressId) {
        String userId = getUserId();
        if (userId == null) {
            _addressDeleteStatus.setValue(false);
            return;
        }

        repository.deleteAddress(userId, addressId)
                .addOnSuccessListener(aVoid -> {
                    _addressDeleteStatus.setValue(true); // Xóa thành công
                })
                .addOnFailureListener(e -> {
                    _addressDeleteStatus.setValue(false); // Xóa thất bại
                });
    }

    public void resetAddressDeleteStatus() {
        _addressDeleteStatus.setValue(null);
    }

    public LiveData<ArrayList<AddressItem>> loadAddresses() {
        String userId = getUserId();
        if (userId == null) {
            return new MutableLiveData<>(new ArrayList<>()); // Trả về rỗng nếu chưa đăng nhập
        }
        return repository.fetchAddresses(userId);
    }

    public void resetAddressSaveStatus() { _addressSaveStatus.setValue(null); }

    public void updateUserProfile(Map<String, Object> dataToUpdate) {
        String userId = getUserId();
        if (userId != null) {
            repository.updateUserProfile(userId, dataToUpdate,
                    // (onSuccess)
                    aVoid -> _profileUpdateStatus.setValue(true),
                    // (onFailure)
                    e -> _profileUpdateStatus.setValue(false)
            );
        } else {
            _profileUpdateStatus.setValue(false); // Thất bại (chưa đăng nhập)
        }
    }

    public void resetProfileUpdateStatus() {
        _profileUpdateStatus.setValue(null);
    }

    public MainViewModel() {

    }

    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public LiveData<Boolean> getFavoriteStatus(String foodId) {
        String userId = getUserId();
        if (userId == null || foodId == null) return new MutableLiveData<>(false);
        return repository.isFavorite(userId, foodId);
    }

    public void toggleFavorite(FoodItem foodItem, boolean isCurrentlyFavorite) {
        String userId = getUserId();
        if (userId != null) {
            repository.toggleFavorite(userId, foodItem, isCurrentlyFavorite);
        }
    }

    public LiveData<ArrayList<FoodItem>> loadFavoriteFoods(){
        String userId = getUserId();
        if (userId == null) return new MutableLiveData<>(new ArrayList<>());
        return repository.fetchFavoriteFoods(userId);
    }

    public void addToCart(FoodItem foodItem, long quantity) {
        String userId = getUserId();
        if (userId != null && foodItem != null) {
            repository.addToCart(userId, foodItem, quantity);
        }
    }

    public void updateCartQuantity(String cartItemId, long newQuantity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            repository.updateCartQuantity(user.getUid(), cartItemId, newQuantity);
        }
    }

    public LiveData<ArrayList<CartItem>> loadCartFoods(){
        String userId = getUserId();
        if (userId == null) return new MutableLiveData<>(new ArrayList<>());
        return repository.fetchCartItems(userId);
    }

    public void removeFromCart(String cartItemId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && cartItemId != null) {
            repository.removeCartItem(user.getUid(), cartItemId);
        }
    }

    public void clearCart() {
        String userId = getUserId();
        if (userId != null) {
            repository.clearCart(userId);
        }
    }


    public LiveData<ArrayList<CategoryItem>> loadCategories(){
        return cachedCategories;
    }

    public LiveData<ArrayList<FoodItem>> loadFoods(){
        return allFoods;
    }

    public LiveData<ArrayList<FoodItem>> loadLimitedFoods(){
        return limitedFoods;
    }

    public LiveData<ArrayList<FoodItem>> loadFoodsByCategoryId(String category_id){
        return repository.fetchFoodsByCategoryId(category_id);
    }

    public LiveData<CategoryItem> loadCategoryInfo(String categoryId) {
        return repository.fetchCategoryInfo(categoryId);
    }

    public LiveData<UserItem> loadUserInfo(String email){
        return repository.fetchUserByEmail(email);
    }

    public LiveData<ArrayList<NotificationItem>> loadNotifications() {
        String userId = getUserId();
        if (userId == null) {
            MutableLiveData<ArrayList<NotificationItem>> emptyList = new MutableLiveData<>();
            emptyList.setValue(new ArrayList<>());
            return emptyList;
        }
        return repository.fetchNotifications(userId);
    }

    public void updateNotificationSetting(UserSessionManager sessionManager, boolean isEnabled) {
        String userId = getUserId();
        if (userId == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("isNotificationEnabled", isEnabled);

        repository.updateUserProfile(
                userId,
                updates,
                aVoid -> {
                    _notificationStatus.setValue(isEnabled);

                    sessionManager.setNotificationEnabled(isEnabled);
                },
                e -> {
                    _notificationStatus.setValue(null);
                }
        );
    }


    public void clearNotifications() {
        String userId = getUserId();
        if (userId != null) {
            repository.clearAllNotifications(userId)
                    .addOnSuccessListener(aVoid -> _notificationClearStatus.setValue(true))
                    .addOnFailureListener(e -> _notificationClearStatus.setValue(false));
        } else {
            _notificationClearStatus.setValue(false);
        }
    }

}
