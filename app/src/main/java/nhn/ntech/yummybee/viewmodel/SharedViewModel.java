package nhn.ntech.yummybee.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import nhn.ntech.yummybee.model.FoodItem;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<String> selectedCategoryId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> triggerOrderTab = new MutableLiveData<>();
    private final MutableLiveData<FoodItem> foodItemLiveData = new MutableLiveData<>();

    public LiveData<FoodItem> getFoodItem() {
        return foodItemLiveData;
    }

    public void setFoodItem(FoodItem item) {
        foodItemLiveData.setValue(item);
    }

    public void setSelectedCategoryId(String categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    public LiveData<String> getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void triggerOrderTab() {
        triggerOrderTab.setValue(true);
    }

    public LiveData<Boolean> getTriggerOrderTab() {
        return triggerOrderTab;
    }

    public void resetTrigger() {
        triggerOrderTab.setValue(false);
    }

    public void resetCategoryId() {
        selectedCategoryId.setValue(null);
    }

}
