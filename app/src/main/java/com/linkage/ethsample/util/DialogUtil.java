package com.linkage.ethsample.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.linkage.ethsample.R;
import com.linkage.ethsample.WalletGenActivity;
import com.linkage.ethsample.interfaces.PasswordDialogCallback;

/**
 * Created by Administrator on 2018\2\7 0007.
 */

public class DialogUtil {

    /**
     * <p>writeDownPassword</p>
     * @param c
     * @description  弹出生成wallet file提示框
     */
    public static void writeDownPassword(final WalletGenActivity c) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= 24) // Otherwise buttons on 7.0+ are nearly invisible
            builder = new AlertDialog.Builder(c, R.style.AlertDialogTheme);
        else
            builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.dialog_write_down_pw_title);
        builder.setMessage(c.getString(R.string.dialog_write_down_pw_text));
        builder.setPositiveButton(R.string.action_sign_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                c.genWalletFile();
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.dialog_back_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * <p>askForPasswordAndDecode</p>
     * @param ac
     * @param fromAddress
     * @param callback
     */
    public static void askForPasswordAndDecode(Activity ac, final String fromAddress, final PasswordDialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ac, R.style.AlertDialogTheme);
        builder.setTitle("Wallet Password");

        final EditText input = new EditText(ac);
        final CheckBox showpw = new CheckBox(ac);
        showpw.setText(R.string.password_in_clear_text);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        LinearLayout container = new LinearLayout(ac);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ac.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin = ac.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.bottomMargin = ac.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = ac.getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.leftMargin = ac.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params2.rightMargin = ac.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(params);
        showpw.setLayoutParams(params2);

        container.addView(input);
        container.addView(showpw);
        builder.setView(container);

        showpw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else
                    input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                input.setSelection(input.getText().length());
            }
        });

        builder.setView(container);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager inputMgr = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager inputMgr = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
                callback.success(input.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager inputMgr = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
                callback.canceled();
                dialog.cancel();
            }
        });

        builder.show();
    }
}
