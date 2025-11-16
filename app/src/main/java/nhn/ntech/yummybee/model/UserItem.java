package nhn.ntech.yummybee.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class UserItem {
    // Dùng để lưu Document ID (UID từ Firebase Auth)
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String birthday;
    private long birthdayMillis;
    private String gender;
    private String city;
    private String role;
    private Timestamp createdAt; // Dùng Timestamp của Firebase
    private String avatarUrl; // Thêm trường ảnh đại diện (từ Cloudinary)

    @PropertyName("isNotificationEnabled")
    private boolean isNotificationEnabled;


    // 2. Constructors
    public UserItem() {
    }

    public UserItem(String uid, String firstName, String lastName, String email,
                    String phoneNumber, String birthday, long birthdayMillis, String gender, String city,
                    String role, Timestamp createdAt, String avatarUrl) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.birthdayMillis = birthdayMillis;
        this.gender = gender;
        this.city = city;
        this.role = role;
        this.createdAt = createdAt;
        this.avatarUrl = avatarUrl;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public long getBirthdayMillis() {
        return birthdayMillis;
    }

    public void setBirthdayMillis(long birthdayMillis) {
        this.birthdayMillis = birthdayMillis;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @PropertyName("isNotificationEnabled")
    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    @PropertyName("isNotificationEnabled")
    public void setNotificationEnabled(boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }
}
