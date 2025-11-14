package nhn.ntech.yummybee.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.CartAdapter;
import nhn.ntech.yummybee.model.CartItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;


public class CartFragment extends Fragment {

    private Toolbar toolbar;
    private MainViewModel mainViewModel;
    private CartAdapter cartAdapter;
    private RecyclerView rvCart;

    private TextView txtPriceSubtotal, txtPriceDelivery, txtPriceTax, txtPriceTotal;
    private AppCompatButton btnCheckOut;
    private ArrayList<CartItem> currentCartList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        close();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        cartAdapter = new CartAdapter(new ArrayList<>(), mainViewModel);
        rvCart.setAdapter(cartAdapter);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        observeCartAndUpdateSummary();
        setBtnCheckoutListener();
    }

    private void setBtnCheckoutListener() {
        btnCheckOut.setOnClickListener(view -> {
            if (currentCartList != null && !currentCartList.isEmpty()){
                Intent intent = new Intent(requireActivity(), CheckOutActivity.class);
                intent.putExtra("CART_LIST_DATA", currentCartList);
                startActivity(intent);
            } else {
                Toast.makeText(requireActivity(), "Giỏ hàng không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeCartAndUpdateSummary() {
        mainViewModel.loadCartFoods().observe(getViewLifecycleOwner(), cartItems -> {
            cartAdapter.setData(cartItems);

            this.currentCartList = cartItems;

            long subtotal = 0;
            if (cartItems != null && !cartItems.isEmpty()){
                for (CartItem cartItem : cartItems){
                    subtotal += cartItem.getPrice() * cartItem.getQuantity();
                }
            }

            long shippingFee = (subtotal > 0) ? 25000 : 0;
            long tax = (long) (subtotal * 0.08);
            long totalAmount = subtotal + shippingFee + tax;

            txtPriceSubtotal.setText(String.format(Locale.getDefault(), "%,d đ", subtotal));
            txtPriceDelivery.setText(String.format(Locale.getDefault(), "%,d đ", shippingFee));
            txtPriceTax.setText(String.format(Locale.getDefault(), "%,d đ", tax));
            txtPriceTotal.setText(String.format(Locale.getDefault(), "%,d đ", totalAmount));
        });
    }

    private void close(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager2 viewPager = requireActivity().findViewById(R.id.mainViewPager);

                // Ra lệnh cho ViewPager2 chuyển về trang 0 (HomeFragment)
                viewPager.setCurrentItem(0, true);
            }
        });
    }

    private void mapping(View view){
        toolbar = view.findViewById(R.id.toolbar);
        rvCart = view.findViewById(R.id.rvCart);
        txtPriceSubtotal = view.findViewById(R.id.txtPriceSubtotal);
        txtPriceDelivery = view.findViewById(R.id.txtPriceDelivery);
        txtPriceTax = view.findViewById(R.id.txtPriceTax);
        txtPriceTotal = view.findViewById(R.id.txtPriceTotal);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
    }
}