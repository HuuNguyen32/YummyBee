package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.CategoryAdapter;
import nhn.ntech.yummybee.adapter.FoodAdapter;
import nhn.ntech.yummybee.manager.UserSessionManager;
import nhn.ntech.yummybee.model.CategoryItem;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.utils.DialogUtils;
import nhn.ntech.yummybee.viewmodel.AuthViewModel;
import nhn.ntech.yummybee.viewmodel.MainViewModel;
import nhn.ntech.yummybee.viewmodel.SharedViewModel;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, FoodAdapter.OnFoodClickListener{

    private ProgressBar progressBarCategories, progressBarFoods;
    private RecyclerView recyclerViewCategories, recyclerViewFoods;
    private DrawerLayout homeDrawerLayout;
    private TextView txtSeeAll, txtUsername, txtDrawerUsername, txtDrawerEmail;
    private Toolbar toolbar;
    private MainViewModel mainViewModel;
    private ActionBarDrawerToggle toggle;
    private FoodAdapter foodAdapter;
    private CategoryAdapter categoryAdapter;
    private NavigationView navigationView;
    private AuthViewModel authViewModel;
    private EditText edtHomeSearch;
    private SharedViewModel sharedViewModel;
    private UserSessionManager userSessionManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        userSessionManager = new UserSessionManager(requireActivity());
        setupToolbarAndDrawer();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        updateDrawerMenu();

        initRecyclerViews();

        observeLimitedFoods();

        observeCategories();

        seeAllFoods();

        setupDrawerListener();

        navigateToSearchActivity();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        txtDrawerUsername.setText(userSessionManager.getFullName());
        txtDrawerEmail.setText(userSessionManager.getEmail());
        txtUsername.setText(getString(R.string.greeting_prefix)+userSessionManager.getLastName());
    }

    private void setupDrawerListener() {
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home){
                // Mặc định là về trang home
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(requireActivity(), SettingActivity.class);
                startActivity(intent);
                homeDrawerLayout.closeDrawers();
                return false;
            } else if (id == R.id.nav_history) {
                Intent intent = new Intent(requireActivity(), OrderHistoryActivity.class);
                startActivity(intent);
                homeDrawerLayout.closeDrawers();
                return false;
            } else if (id == R.id.nav_share) {
                homeDrawerLayout.closeDrawers();
                return false;
            } else if (id == R.id.nav_about) {
                Intent intent = new Intent(requireActivity(), AboutUsActivity.class);
                startActivity(intent);
                homeDrawerLayout.closeDrawers();
                return false;
            } else if (id == R.id.nav_logout) {
                DialogUtils.showCustomDialogBox(requireActivity(), getString(R.string.app_name),getString(R.string.logout_confirm_message), false, () ->{
                    authViewModel.logout();
                    userSessionManager.clearSession();
                    updateDrawerMenu();
                    navToLoginScreen();
                });
                return false;
            } else if (id == R.id.nav_login) {
                navToLoginScreen();
            }
            homeDrawerLayout.closeDrawers();

            return true;
        });
    }

    private void navToLoginScreen(){
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateDrawerMenu(){
        if (navigationView == null) return;
        Menu menu = navigationView.getMenu();

        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);

        boolean isLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        loginItem.setVisible(!isLoggedIn);
        logoutItem.setVisible(isLoggedIn);

    }

    private void navigateToSearchActivity(){
        edtHomeSearch.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            startActivity(intent);

        });
    }

    private void seeAllFoods(){
        txtSeeAll.setOnClickListener(view -> {
           sharedViewModel.setSelectedCategoryId(null);
           sharedViewModel.triggerOrderTab();
        });
    }

    private void navigateFragment(String category_id){
        OrderFragment orderFragment = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category_id", category_id);
        orderFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.main, orderFragment)
                .addToBackStack(null)
                .commit();
    }

    private void initRecyclerViews() {
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
        ));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        recyclerViewCategories.setAdapter(categoryAdapter);
        recyclerViewCategories.setNestedScrollingEnabled(true);

        recyclerViewFoods.setLayoutManager(new GridLayoutManager(
                requireContext(),
                2
        ));
        foodAdapter = new FoodAdapter(new ArrayList<>(), this);
        recyclerViewFoods.setAdapter(foodAdapter);
        recyclerViewFoods.setNestedScrollingEnabled(false);
        divideTheTouchArea();
    }

    private void divideTheTouchArea(){
        recyclerViewCategories.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Khi người dùng BẮT ĐẦU chạm vào RecyclerView (ACTION_DOWN)
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    // Yêu cầu View cha (ScrollView) KHÔNG can thiệp vào cử chỉ này
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                }
                // Khi người dùng NHẤC NGÓN TAY LÊN (ACTION_UP) hoặc cử chỉ bị hủy
                else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Cho phép View cha can thiệp trở lại (cho lần cuộn sau)
                    rv.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false; // Luôn trả về false để RecyclerView tự xử lý
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });
    }

    private void observeCategories() {
        progressBarCategories.setVisibility(View.VISIBLE);
        mainViewModel.loadCategories().observe(getViewLifecycleOwner(), categoryItems -> {
            if (categoryItems != null && !categoryItems.isEmpty()) {
                categoryAdapter.setCategories(categoryItems);
            }
            progressBarCategories.setVisibility(View.GONE);
        });
    }

    private void observeLimitedFoods() {
        progressBarFoods.setVisibility(View.VISIBLE);
        mainViewModel.loadLimitedFoods().observe(getViewLifecycleOwner(), foodItems -> {
            if (foodItems != null && !foodItems.isEmpty()) {
                foodAdapter.setFoods(foodItems);
            }
            progressBarFoods.setVisibility(View.GONE);
        });
    }

    private void setupToolbarAndDrawer() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(requireActivity(), homeDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        homeDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.optionFavorite) {
            Intent intent = new Intent(requireActivity(), FavouriteListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.optionNotification) {
            Intent intent = new Intent(requireActivity(), NotificationActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void mapping(View view) {
        homeDrawerLayout = view.findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.toolbar);
        progressBarCategories = view.findViewById(R.id.progressBarCategories);
        progressBarFoods = view.findViewById(R.id.progressBarFoods);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerViewFoods = view.findViewById(R.id.recyclerViewFoods);
        txtSeeAll = view.findViewById(R.id.txtSeeAll);
        txtUsername = view.findViewById(R.id.txtUsername);
        navigationView = view.findViewById(R.id.navigation_view);
        edtHomeSearch = view.findViewById(R.id.edtHomeSearch);

        // Lấy header view của NavigationView
        View headerView = navigationView.getHeaderView(0);
        txtDrawerUsername = headerView.findViewById(R.id.txtDrawerUsername);
        txtDrawerEmail = headerView.findViewById(R.id.txtDrawerEmail);
    }

    @Override
    public void onCategoryClick(CategoryItem category) {
        sharedViewModel.setSelectedCategoryId(category.getId());
        sharedViewModel.triggerOrderTab();
    }

    @Override
    public void onAddToCartClick(FoodItem foodItem) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Đã đăng nhập -> Thêm vào giỏ hàng
            mainViewModel.addToCart(foodItem, 1);
            Toast.makeText(requireActivity(), getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
        } else {
            // Chưa đăng nhập -> Yêu cầu đăng nhập
            showLoginPrompt();
        }
    }

    private void showLoginPrompt() {
        Toast.makeText(requireContext(), getString(R.string.login_required_to_add_cart), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
    }
}