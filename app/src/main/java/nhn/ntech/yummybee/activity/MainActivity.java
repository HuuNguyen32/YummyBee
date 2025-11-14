package nhn.ntech.yummybee.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.ViewPagerAdapter;
import nhn.ntech.yummybee.viewmodel.SharedViewModel;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mainViewPager;
    private BottomNavigationView bottomNavigationView;
    private FragmentStateAdapter viewPagerAdapter;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mapping();
        viewPagerAdapter = new ViewPagerAdapter(this);
        mainViewPager.setAdapter(viewPagerAdapter);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getTriggerOrderTab().observe(this, result -> {
            if (result != null && result) {
                mainViewPager.setCurrentItem(1, true); // chuyển sang tab Order
                sharedViewModel.resetTrigger(); // reset để tránh lặp lại
            }
        });

        mainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navOrder);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navCart);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.navProfile);
                        break;
                    default:
                        bottomNavigationView.setSelectedItemId(R.id.navHome);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navHome){
                    mainViewPager.setCurrentItem(0, true);
                    return true;
                } else if (id == R.id.navOrder) {
                    sharedViewModel.resetCategoryId();
                    mainViewPager.setCurrentItem(1, true);
                    return true;
                } else if (id == R.id.navCart) {
                    mainViewPager.setCurrentItem(2, true);
                    return true;
                } else if (id == R.id.navProfile) {
                    mainViewPager.setCurrentItem(3, true);
                    return true;
                }
                return false;
            }
        });
    }


    private void mapping(){
        mainViewPager = findViewById(R.id.mainViewPager);
        bottomNavigationView = findViewById(R.id.mainBottomNavigationView);
    }
}