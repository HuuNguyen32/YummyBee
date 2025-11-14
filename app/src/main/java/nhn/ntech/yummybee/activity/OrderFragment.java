package nhn.ntech.yummybee.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.FoodAdapter;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;
import nhn.ntech.yummybee.viewmodel.SharedViewModel;

public class OrderFragment extends Fragment implements FoodAdapter.OnFoodClickListener {

    private RecyclerView recyclerViewAllFoods;
    private ProgressBar progressBarAllFoods;
    private MainViewModel mainViewModel;
    private Toolbar toolbar;
    private TextView txtTitleToolbar;
    private FoodAdapter foodAdapter;
    private SharedViewModel sharedViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mapping(view);
        back();
        initRecyclerViews();
        sharedViewModel.getSelectedCategoryId().observe(getViewLifecycleOwner(), this::observeSharedData);

    }

    private void back(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedViewModel.resetCategoryId();
                ViewPager2 viewPager = requireActivity().findViewById(R.id.mainViewPager);
                viewPager.setCurrentItem(0, true);
            }
        });
    }

    private void observeSharedData(String category_id){
            if (category_id == null || category_id.isEmpty()){
                txtTitleToolbar.setText(getString(R.string.all_products));
                observeAllFoods();
            }else{
                observeFoodsByCategoryId(category_id);
                mainViewModel.loadCategoryInfo(category_id).observe(getViewLifecycleOwner(), categoryItem -> {
                    if (categoryItem != null) {
                        txtTitleToolbar.setText(categoryItem.getName());
                    } else {
                        txtTitleToolbar.setText(getString(R.string.products)); // Tên dự phòng
                    }
                });
            }

    }

    private void initRecyclerViews() {
        recyclerViewAllFoods.setLayoutManager(new GridLayoutManager(
                requireContext(),
                2
        ));
        foodAdapter = new FoodAdapter(new ArrayList<>(), this);
        recyclerViewAllFoods.setAdapter(foodAdapter);
        recyclerViewAllFoods.setNestedScrollingEnabled(true);
    }

    private void observeAllFoods() {
        progressBarAllFoods.setVisibility(View.VISIBLE);
        mainViewModel.loadFoods().observe(getViewLifecycleOwner(), foodItems -> {
            if (foodItems != null && !foodItems.isEmpty()) {
                foodAdapter.setFoods(foodItems);
            }
            progressBarAllFoods.setVisibility(View.GONE);
        });
    }

    private void observeFoodsByCategoryId(String category_id) {
        progressBarAllFoods.setVisibility(View.VISIBLE);
        mainViewModel.loadFoodsByCategoryId(category_id).observe(getViewLifecycleOwner(), foodItems -> {
            if (foodItems != null && !foodItems.isEmpty()) {
                foodAdapter.setFoods(foodItems);
            }
            progressBarAllFoods.setVisibility(View.GONE);
        });
    }

    private void mapping(View view){
        toolbar = view.findViewById(R.id.toolbar);
        txtTitleToolbar = view.findViewById(R.id.txtTitleToolbar);
        recyclerViewAllFoods = view.findViewById(R.id.recyclerViewAllFoods);
        progressBarAllFoods = view.findViewById(R.id.progressBarAllFoods);
    }

    @Override
    public void onAddToCartClick(FoodItem foodItem) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Đã đăng nhập -> Thêm vào giỏ hàng
            mainViewModel.addToCart(foodItem, 1);
            Toast.makeText(requireActivity(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        } else {
            // Chưa đăng nhập -> Yêu cầu đăng nhập
            showLoginPrompt();
        }
    }

    private void showLoginPrompt() {
        Toast.makeText(requireActivity(), "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
    }
}