package nhn.ntech.yummybee.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import nhn.ntech.yummybee.R;


public class HomeFragment extends Fragment {


    private DrawerLayout homeDrawerLayout;
    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mapping(view);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(requireActivity(), homeDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        homeDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.optionFavorite){
            // Xử lý yêu thích
            return true;
        } else if (id == R.id.optionNotification) {
            // Xử lý thông báo
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void mapping(View view){
        homeDrawerLayout = view.findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.toolbar);
    }
}