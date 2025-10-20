package nhn.ntech.yummybee.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import nhn.ntech.yummybee.activity.CartFragment;
import nhn.ntech.yummybee.activity.HomeFragment;
import nhn.ntech.yummybee.activity.OrderFragment;
import nhn.ntech.yummybee.activity.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position){
           case 1:
               return new OrderFragment();
           case 2:
               return new CartFragment();
           case 3:
               return new ProfileFragment();
           default:
               return new HomeFragment();
       }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
