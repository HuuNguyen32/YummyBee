package nhn.ntech.yummybee.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.FoodAdapter;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;
import nhn.ntech.yummybee.viewmodel.SharedViewModel;

public class SearchActivity extends AppCompatActivity implements FoodAdapter.OnFoodClickListener {

    private MainViewModel viewModel;
    private ImageView backBtn;
    private EditText edtSearchBar;
    private ListView searchListView;
    private RecyclerView recyclerSearchResult;

    private ArrayAdapter<String> searchAdapter;
    private FoodAdapter foodAdapter;

    private List<String> fullNameList;
    private List<String> filteredNameList;
    private List<FoodItem> foodList;
    private SharedViewModel sharedViewModel;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mapping();
        setupInsets();
        setupViewModel();
        setupAdapters();
        setupListeners();
    }

    private void mapping() {
        backBtn = findViewById(R.id.backBtn);
        edtSearchBar = findViewById(R.id.edtSearchBar);
        searchListView = findViewById(R.id.searchListView);
        recyclerSearchResult = findViewById(R.id.recyclerSearchResult);
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        fullNameList = new ArrayList<>();
        filteredNameList = new ArrayList<>();
        foodList = new ArrayList<>();

        viewModel.loadFoods().observe(this, foodItems -> {
            fullNameList.clear();
            foodList.clear();
            foodList.addAll(foodItems);
            for (FoodItem item : foodItems) {
                fullNameList.add(item.getName());
            }
        });
    }

    private void setupAdapters() {
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredNameList);
        searchListView.setAdapter(searchAdapter);

        foodAdapter = new FoodAdapter(new ArrayList<>(),this);
        recyclerSearchResult.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        recyclerSearchResult.setAdapter(foodAdapter);
    }

    private void setupListeners() {
        edtSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleSearchTextChanged(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        edtSearchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                showSearchResults(edtSearchBar.getText().toString());
                return true;
            }
            return false;
        });

        searchListView.setOnItemClickListener((adapterView, view, i, l) -> {
            String keyword = filteredNameList.get(i);
            edtSearchBar.setText(keyword);
            showSearchResults(keyword);
        });

        edtSearchBar.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtSearchBar.getRight() - edtSearchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    edtSearchBar.setText("");
                    searchListView.setVisibility(View.GONE);
                    recyclerSearchResult.setVisibility(View.GONE);
                    return true;
                }
            }
            return false;
        });

        backBtn.setOnClickListener(view -> {
            if (recyclerSearchResult.getVisibility() == View.VISIBLE) {
                recyclerSearchResult.setVisibility(View.GONE);
                restoreSuggestionList();
            } else {
                finish();
            }
        });
    }

    private void handleSearchTextChanged(String keyword) {
        keyword = keyword.toLowerCase().trim();
        filteredNameList.clear();

        if (!keyword.isEmpty()) {
            for (String name : fullNameList) {
                if (name.toLowerCase().contains(keyword)) {
                    filteredNameList.add(name);
                }
            }
            searchListView.setVisibility(View.VISIBLE);
            recyclerSearchResult.setVisibility(View.GONE);
        } else {
            searchListView.setVisibility(View.GONE);
            recyclerSearchResult.setVisibility(View.GONE);
        }

        searchAdapter.notifyDataSetChanged();
    }

    private void showSearchResults(String keyword) {
        keyword = keyword.toLowerCase().trim();
        ArrayList<FoodItem> filteredFoodList = new ArrayList<>();

        for (FoodItem item : foodList) {
            if (item.getName().toLowerCase().contains(keyword)) {
                filteredFoodList.add(item);
            }
        }

        searchListView.setVisibility(View.GONE);
        foodAdapter.setFoods(filteredFoodList);
        recyclerSearchResult.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearchBar.getWindowToken(), 0);
    }

    private void restoreSuggestionList() {
        String keyword = edtSearchBar.getText().toString().toLowerCase().trim();
        filteredNameList.clear();

        if (!keyword.isEmpty()) {
            for (String name : fullNameList) {
                if (name.toLowerCase().contains(keyword)) {
                    filteredNameList.add(name);
                }
            }
            searchListView.setVisibility(View.VISIBLE);
        } else {
            searchListView.setVisibility(View.GONE);
        }

        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddToCartClick(FoodItem foodItem) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Đã đăng nhập -> Thêm vào giỏ hàng
            mainViewModel.addToCart(foodItem, 1);
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        } else {
            // Chưa đăng nhập -> Yêu cầu đăng nhập
            showLoginPrompt();
        }
    }
    private void showLoginPrompt() {
        Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
        // (Bạn nên dùng ActivityResultLauncher ở đây)
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}