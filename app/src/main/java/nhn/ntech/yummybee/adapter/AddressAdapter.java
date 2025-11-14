package nhn.ntech.yummybee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.AddressItem;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {


    private ArrayList<AddressItem> addressItems;

    private final OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onAddressSelected(AddressItem address);
    }

    public AddressAdapter(ArrayList<AddressItem> addressItems, OnAddressSelectedListener listener) {
        this.addressItems = addressItems != null ? addressItems : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        AddressItem item = addressItems.get(position);
        String nameAndPhone = item.getFullName() + " | " + item.getPhone();
        holder.txtFullNameAndPhone.setText(nameAndPhone);

        String fullAddress = item.getStreet() + ", " + item.getWard() +
                ", " + item.getDistrict() + ", " + item.getCity();
        holder.txtFullAddress.setText(fullAddress);

        if (item.isDefault()) {
            holder.txtDefaultBadge.setVisibility(View.VISIBLE);
        } else {
            holder.txtDefaultBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressSelected(item);
            }
        });
    }

    public void setAddresses(ArrayList<AddressItem> newAddresses) {
        this.addressItems = (newAddresses != null) ? newAddresses : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return addressItems != null ? addressItems.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtFullNameAndPhone, txtFullAddress, txtDefaultBadge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFullNameAndPhone = itemView.findViewById(R.id.txtFullNameAndPhone);
            txtFullAddress = itemView.findViewById(R.id.txtFullAddress);
            txtDefaultBadge = itemView.findViewById(R.id.txtDefaultBadge);
        }
    }
}
