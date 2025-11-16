package nhn.ntech.yummybee.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nhn.ntech.yummybee.utils.DialogUtils;

public class AuthViewModel extends AndroidViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> deleteError = new MutableLiveData<>();

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }

    public LiveData<String> getDeleteError() {
        return deleteError;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
    }

    public void login(String email, String password, AuthCallBack callBack){
        if (email.isEmpty() || password.isEmpty()){
            callBack.onFailure("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                      user = auth.getCurrentUser();
                        if (user != null){
                            callBack.onSuccess();
                        }else {
                            callBack.onFailure("Vui lòng xác thực email trước khi đăng nhập");
                        }
                    }else {
                        callBack.onFailure("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin đăng nhập");
                    }
                });

    }


    public void register(String firstName, String lastName, String email, String password, String confirmPassword,
                         String phoneNumber, String birthday, long birthdayMillis, String gender, String city ,AuthCallBack callBack){
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || phoneNumber.isEmpty() || birthday.isEmpty() || gender.isEmpty() || city.isEmpty()){
            callBack.onFailure("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if(!password.equals(confirmPassword)){
            callBack.onFailure("Mật khẩu xác nhận không chính xác");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                        Map<String, Object> userMap =new HashMap<>();
                        userMap.put("firstName", firstName);
                        userMap.put("lastName", lastName);
                        userMap.put("email", email);
                        userMap.put("phoneNumber", phoneNumber);
                        userMap.put("birthday", birthday);
                        userMap.put("birthdayMillis", birthdayMillis);
                        userMap.put("gender", gender);
                        userMap.put("city", city);
                        userMap.put("role","user");
                        userMap.put("createdAt", FieldValue.serverTimestamp());
                        userMap.put("isNotificationEnabled", true);

                        firestore.collection("users")
                                .document(userId)
                                .set(userMap)
                                .addOnSuccessListener(unused -> callBack.onSuccess())
                                .addOnFailureListener(e -> callBack.onFailure("Lỗi khi lưu thông tin: "+e.getMessage()));
                    }else{
                        callBack.onFailure("Đăng ký thất bại");
                    }
                });
    }


    public void resetPassword(String email, AuthCallBack callBack){
        if (email.isEmpty()){
            callBack.onFailure("Vui lòng nhập email");
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        callBack.onSuccess();
                    }else {
                        Exception e = task.getException();
                        Log.e("ResetPassword", "Lỗi gửi email", e);
                        callBack.onFailure("Gửi email thất bại: " + e.getMessage());
                    }
                });
    }


    public void deleteAccount(String password, DeleteCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        DialogUtils.PasswordHolder.password = "";

        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

            user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                if (authTask.isSuccessful()) {
                    firestore.collection("users").document(user.getUid())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                user.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure("Xóa tài khoản thất bại: " + deleteTask.getException().getMessage());
                                    }
                                });
                            })
                            .addOnFailureListener(e -> callback.onFailure("Lỗi khi xóa dữ liệu Firestore: " + e.getMessage()));
                } else {
                    callback.onFailure("Xác thực thất bại: " + authTask.getException().getMessage());
                }
            });
        } else {
            callback.onFailure("Không tìm thấy người dùng hiện tại.");
        }
    }



    public void logout(){
        if (auth.getCurrentUser() != null){
            auth.signOut();
        }
    }

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface AuthCallBack{
        void onSuccess();
        void onFailure(String message);
    }
}
