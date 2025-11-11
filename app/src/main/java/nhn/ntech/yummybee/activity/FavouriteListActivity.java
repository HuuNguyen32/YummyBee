package nhn.ntech.yummybee.activity;

import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.FavoriteAdapter;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class FavouriteListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MainViewModel mainViewModel;
    private RecyclerView rvFavoriteList;
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourite_list);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        favoriteAdapter = new FavoriteAdapter(new ArrayList<>(), mainViewModel);
        rvFavoriteList.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteList.setAdapter(favoriteAdapter);

        mainViewModel.loadFavoriteFoods().observe(this, foodItems -> {
            favoriteAdapter.setData(foodItems);
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        rvFavoriteList = findViewById(R.id.rvFavoriteList);
    }
}