package com.linkage.ethsample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.linkage.ethsample.adapter.WalletAdapter;
import com.linkage.ethsample.base.BaseAppCompatActivity;
import com.linkage.ethsample.bean.WalletDisplay;
import com.linkage.ethsample.interfaces.StorableWallet;
import com.linkage.ethsample.service.TransactionService;
import com.linkage.ethsample.service.WalletGenService;
import com.linkage.ethsample.util.AddressNameConverter;
import com.linkage.ethsample.util.ExternalStorageHandler;
import com.linkage.ethsample.util.Settings;
import com.linkage.ethsample.util.WalletStorage;

import java.math.BigInteger;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    @BindView(R.id.gen_fab)
    FloatingActionButton genFab;
    @BindView(R.id.add_fab)
    FloatingActionButton addFab;
    @BindView(R.id.fabmenu)
    FloatingActionMenu fabmenu;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srlMain;

    protected WalletAdapter walletAdapter;
    protected List<WalletDisplay> wallets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        final ArrayList<StorableWallet> storedwallets = new ArrayList<StorableWallet>(WalletStorage.getInstance(this).get());
        final List<WalletDisplay> w = new ArrayList<WalletDisplay>();
        for (StorableWallet cur : storedwallets)
            w.add(new WalletDisplay(AddressNameConverter.getInstance(this).get(cur.getPubKey()), cur.getPubKey(), new BigInteger("-1"), WalletDisplay.CONTACT));
        wallets.addAll(w);
        walletAdapter.notifyDataSetChanged();
        srlMain.setEnabled(false);
    }

    /**
     * <p>initView</p>
     * @description  初始化界面
     */
    private void initView(){
        walletAdapter = new WalletAdapter(wallets, this, this, this);
        LinearLayoutManager mgr = new LinearLayoutManager(MainActivity.this.getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = mgr;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(walletAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mgr.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    @OnClick({R.id.gen_fab, R.id.add_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gen_fab:
                generateDialog();
                break;
            case R.id.add_fab:
                startActivityForResult(new Intent(MainActivity.this, TransactionActivity.class), TransactionActivity.REQUEST_CODE);
                break;
        }
    }

    /**
     * <p>generateDialog</p>
     *
     * @description 生成wallet file弹出框
     */
    public void generateDialog() {
        if (!Settings.walletBeingGenerated) {
            Intent genI = new Intent(MainActivity.this, WalletGenActivity.class);
            startActivityForResult(genI, WalletGenActivity.REQUEST_CODE);
        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= 24) // Otherwise buttons on 7.0+ are nearly invisible
                builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
            else
                builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.wallet_one_at_a_time);
            builder.setMessage(R.string.wallet_one_at_a_time_text);
            builder.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WalletGenActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent generatingService = new Intent(this, WalletGenService.class);
                generatingService.putExtra("PASSWORD", data.getStringExtra("PASSWORD"));
                startService(generatingService);
            }
        } else if (requestCode == TransactionActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, TransactionService.class);
                intent.putExtra("PASSWORD", data.getStringExtra("PASSWORD"));
                intent.putExtra("NODEADDRESS", data.getStringExtra("NODEADDRESS"));
                intent.putExtra("TOADDRESS", data.getStringExtra("TOADDRESS"));
                intent.putExtra("FROMADDRESS", data.getStringExtra("FROMADDRESS"));
                intent.putExtra("AMOUNT", data.getStringExtra("AMOUNT"));
                startService(intent);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int itemPosition = recyclerView.getChildLayoutPosition(view);
        if (itemPosition >= wallets.size())
            return;
        Intent detail = new Intent(MainActivity.this, WalletDetailActivity.class);
        detail.putExtra("ADDRESS", wallets.get(itemPosition).getPublicKey());
        startActivity(detail);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
