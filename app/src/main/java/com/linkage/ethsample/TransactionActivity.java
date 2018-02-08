package com.linkage.ethsample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.linkage.ethsample.base.BaseAppCompatActivity;
import com.linkage.ethsample.interfaces.PasswordDialogCallback;
import com.linkage.ethsample.util.DialogUtil;
import com.linkage.ethsample.util.WalletStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactionActivity extends BaseAppCompatActivity {

    @BindView(R.id.tv_node)
    EditText tvNode;
    @BindView(R.id.toAddress)
    EditText toAddress;
    @BindView(R.id.et_amount)
    EditText amount;
    @BindView(R.id.btn_transfer)
    Button btnTransfer;
    @BindView(R.id.layout_transfer)
    LinearLayout layoutTransfer;

    public static final int REQUEST_CODE = 402;
    @BindView(R.id.spinner)
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    /**
     * <p>initView</p>
     * @description 初始化界面
     */
    private void initView(){
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(TransactionActivity.this, R.layout.address_spinner, WalletStorage.getInstance(TransactionActivity.this).getFullOnly()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }


    @OnClick(R.id.btn_transfer)
    public void onViewClicked() {
        String nodetxt = tvNode.getText().toString().trim();
        String addressTxt = toAddress.getText().toString().trim();
        String amounttxt = amount.getText().toString().trim();
        String fromtxt = spinner.getSelectedItem().toString().toLowerCase();

        /*if(!isPasswordValid(pwdtxt)){
            snackError(layoutTransfer,getResources().getString(R.string.error_invalid_password));
            return;
        }*/

        if (TextUtils.isEmpty(nodetxt) || TextUtils.isEmpty(addressTxt) || TextUtils.isEmpty(amounttxt) || TextUtils.isEmpty(fromtxt)) {
            snackError(layoutTransfer, getResources().getString(R.string.error_invalid_input));
            return;
        }

        DialogUtil.askForPasswordAndDecode(this, fromtxt, new PasswordDialogCallback() {
            @Override
            public void success(String password) {
                transferBTH(password);
            }

            @Override
            public void canceled() {

            }
        });
    }

    public void transferBTH(String pwdtxt) {
        Intent data = new Intent();
        data.putExtra("PASSWORD", pwdtxt);
        data.putExtra("NODEADDRESS", tvNode.getText().toString().trim());
        data.putExtra("TOADDRESS", toAddress.getText().toString().trim());
        data.putExtra("FROMADDRESS", spinner.getSelectedItem().toString().toLowerCase().trim());
        data.putExtra("AMOUNT", Integer.parseInt(amount.getText().toString().trim()));
        setResult(RESULT_OK, data);
        finish();
    }
}
