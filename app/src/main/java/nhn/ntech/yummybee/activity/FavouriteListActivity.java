package nhn.ntech.yummybee.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.FavoriteAdapter;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.utils.DialogUtils;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class FavouriteListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MainViewModel mainViewModel;
    private RecyclerView rvFavoriteList;
    private FavoriteAdapter favoriteAdapter;
    private TextView txtMessageEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourite_list);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupToolbar();
        initRecycleView();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            observeFavourite();
            observeDeleteAllFavourite();
        }
        else{
            txtMessageEmpty.setText("Vui lòng đăng nhập để xem yêu thích");
            txtMessageEmpty.setVisibility(View.VISIBLE);
            rvFavoriteList.setVisibility(View.GONE);
        }
    }

    private void initRecycleView(){
        rvFavoriteList.setLayoutManager(new LinearLayoutManager(this));
        favoriteAdapter = new FavoriteAdapter(new ArrayList<>(), mainViewModel);
        rvFavoriteList.setAdapter(favoriteAdapter);
    }

    private void observeFavourite(){
        mainViewModel.loadFavoriteFoods().observe(this, foodItems -> {
            if (foodItems != null && !foodItems.isEmpty()){
                rvFavoriteList.setVisibility(View.VISIBLE);
                txtMessageEmpty.setVisibility(View.GONE);
                favoriteAdapter.setData(foodItems);
            }else{
                rvFavoriteList.setVisibility(View.GONE);
                txtMessageEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_more_vert_24));
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_all_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_delete_all){
            DialogUtils.showCustomDialogBox(
                    this,
                    "Xóa tất cả yêu thích",
                    "Bạn có chắc chắn muốn xóa tất cả yêu thích?",
                    false,
                    () -> {
                        mainViewModel.clearFavourite();
                    }
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void observeDeleteAllFavourite(){
        mainViewModel.getFavouriteClearStatus().observe(this, aBoolean -> {
            if (aBoolean){
                Toast.makeText(this, "Đã xóa tất cả yêu thích.", Toast.LENGTH_SHORT).show();
                observeFavourite();
            }
        });
    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        rvFavoriteList = findViewById(R.id.rvFavoriteList);
        txtMessageEmpty = findViewById(R.id.txtMessageEmpty);
    }
}