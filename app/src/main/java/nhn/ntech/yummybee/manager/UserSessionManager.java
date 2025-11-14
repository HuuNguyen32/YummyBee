package nhn.ntech.yummybee.manager;

import android.content.Context;
import android.content.SharedPreferences;

import nhn.ntech.yummybee.R;

public class UserSessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUserInfo(String firstName, String lastName, String email,
                             String phoneNumber, String birthday, long birthdayMillis, String gender, String city) {
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("birthday", birthday);
        editor.putLong("birthdayMillis", birthdayMillis);
        editor.putString("gender", gender);
        editor.putString("city", city);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    public String getFirstName() { return prefs.getString("firstName", "Khách hàng"); }
    public String getLastName() { return prefs.getString("lastName", ""); }
    public String getFullName() { return getFirstName() + " " + getLastName(); }
    public String getEmail() { return prefs.getString("email", "xxx000@gmail.com"); }
    public String getPhoneNumber() { return prefs.getString("phoneNumber", ""); }
    public String getBirthday() { return prefs.getString("birthday", ""); }
    public long getBirthdayMillis() { return prefs.getLong("birthdayMillis", 0L);}

    public String getGender() { return prefs.getString("gender", ""); }
    public String getCity() { return prefs.getString("city", ""); }
    public boolean isLoggedIn() { return prefs.getBoolean("isLoggedIn", false); }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
