package nhn.ntech.yummybee.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.manager.UserSessionManager;


public class ProfileFragment extends Fragment {

    private LinearLayout editProfileLayout, notificationLayout, addressLayout, changePasswordLayout, settingsLayout;
    private ImageView btnClose, imgAvatar;
    private TextView txtUsername;
    private UserSessionManager userSessionManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        userSessionManager = new UserSessionManager(requireContext());
        closeProfile();
        navigateScreen();

    }

    private void navigateScreen() {
        setupEditProfileNavigation();
        setupNotificationNavigation();
        setupAddressNavigation();
        setupChangePasswordNavigation();
        setupSettingsNavigation();
    }

    private void setupEditProfileNavigation() {
        editProfileLayout.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                startActivity(new Intent(requireActivity(), EditProfileActivity.class));
            }else{
                showLoginPrompt();
            }
        });
    }

    private void setupNotificationNavigation() {
        notificationLayout.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                startActivity(new Intent(requireActivity(), NotificationActivity.class));
            }else{
                showLoginPrompt();
            }
        });
    }

    private void setupAddressNavigation(){
        addressLayout.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent = new Intent(requireActivity(), AddressActivity.class);
                intent.putExtra(AddressActivity.MODE_KEY, AddressActivity.MODE_MANAGER);
                startActivity(intent);
            }else{
                showLoginPrompt();
            }
        });
    }

    private void setupChangePasswordNavigation() {
        changePasswordLayout.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
            }else{
                showLoginPrompt();
            }
        });
    }

    private void setupSettingsNavigation() {
        settingsLayout.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), SettingActivity.class));
        });
    }

    private void showLoginPrompt() {
        Toast.makeText(requireContext(), "Vui lòng đăng nhập để xử dụng chức năng.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        txtUsername.setText(userSessionManager.getFullName());
    }

    private void closeProfile() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager2 viewPager = requireActivity().findViewById(R.id.mainViewPager);

                // Ra lệnh cho ViewPager2 chuyển về trang 0 (HomeFragment)
                viewPager.setCurrentItem(0, true);
            }
        });
    }

    private void mapping(View view){
        editProfileLayout = view.findViewById(R.id.editProfileLayout);
        notificationLayout = view.findViewById(R.id.notificationLayout);
        addressLayout = view.findViewById(R.id.addressLayout);
        changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
        settingsLayout = view.findViewById(R.id.settingsLayout);
        btnClose = view.findViewById(R.id.btnClose);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtUsername = view.findViewById(R.id.txtUsername);
    }
}