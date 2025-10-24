package nhn.ntech.yummybee.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import nhn.ntech.yummybee.model.CategoryItem;
import nhn.ntech.yummybee.model.FoodItem;

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
}