package nhn.ntech.yummybee.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class AddressItem implements Serializable {

    @Exclude
    private String id; // Dùng để lưu Document ID

    private String fullName;
    private String phone;
    private String street;
    private String ward;
    private String district;
    private String city;

    @PropertyName("isDefault")
    private boolean isDefault;

    public AddressItem() {

    }

    public AddressItem(String fullName, String phone, String street, String ward, String district, String city, boolean isDefault) {
        this.fullName = fullName;
        this.phone = phone;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
    }

    // --- Getters and Setters ---

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @PropertyName("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    @PropertyName("isDefault")
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return fullName + " - " + phone + "\n"
                + street + ", " + ward + ", " + district + ", " + city;
    }

    public String getFullAddress() {
        return street + ", " + ward + ", " + district + ", " + city;
    }
}
