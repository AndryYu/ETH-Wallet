package com.linkage.ethsample.base;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * <p>isPasswordValid</p>
     * @param password
     * @return
     * @description  判断密码长度
     */
    public boolean isPasswordValid(String password) {
        return password.length() >= 9;
    }

    /**
     * <p>snackError</p>
     * @param s
     * @description snackbar提示
     */
    public void snackError(LinearLayout layout, String s) {
        if (layout == null) return;
        Snackbar mySnackbar = Snackbar.make(layout, s, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
