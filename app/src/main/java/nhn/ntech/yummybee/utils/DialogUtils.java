package nhn.ntech.yummybee.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import nhn.ntech.yummybee.R;

public class DialogUtils {
    public static void showCustomDialogBox(Context context, String title, String message, boolean requiresPassword, Runnable runnable){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_custom_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        EditText editPassword = dialog.findViewById(R.id.edtPassword);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        txtTitle.setText(title);
        txtMessage.setText(message);

        if (!requiresPassword){
            editPassword.setVisibility(View.GONE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {
            if (requiresPassword){
                String password = editPassword.getText().toString().trim();
                if (password.isEmpty()){
                    Toast.makeText(context, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }
                PasswordHolder.password = password;
            }
            else{
                PasswordHolder.password = "";
            }
            runnable.run();
            dialog.dismiss();
        });

        dialog.show();

    }

    public static class PasswordHolder {
        public static String password = "";
    }


}
