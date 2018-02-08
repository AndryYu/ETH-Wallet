package com.linkage.ethsample;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.ethsample.base.BaseAppCompatActivity;
import com.linkage.ethsample.data.Constants;
import com.linkage.ethsample.util.Blockies;
import com.linkage.ethsample.util.Settings;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;

public class WalletDetailActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_account)
    ImageView ivAccount;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_trancount)
    TextView tvTrancount;
    private String address;

    private Web3j web3j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);
        ButterKnife.bind(this);

        address = getIntent().getStringExtra("ADDRESS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
    }

    /**
     * <p>initView</p>
     *
     * @description 初始化界面视图
     */
    private void initView() {
        ivAccount.setImageBitmap(Blockies.createIcon(address));
        tvAddress.setText(address);
    }

    /**
     * <p>initData</p>
     *
     * @Description 初始化数据
     */
    private void initData() {
        web3j = Web3jFactory.build(new HttpService(Constants.NODE_ADDRESS_URL));
        Single.fromCallable(() -> getBalance())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleSubscriber<BigInteger>() {
                    @Override
                    public void onSuccess(BigInteger bigInteger) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String balance = String.valueOf(bigInteger);
                                if (balance.length() > 18) {
                                    tvAmount.setText(balance.substring(0, balance.length() - 18) + "." + balance.substring(balance.length() - 18) + " ETH");
                                } else {
                                    tvAmount.setText(balance + " WEI");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });

        Single.fromCallable(() -> getTransactionList())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleSubscriber<BigInteger>() {
                    @Override
                    public void onSuccess(BigInteger bigInteger) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String balance = String.valueOf(bigInteger);
                                tvTrancount.setText("总共交易次数： " + balance);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }

    /**
     * <p>getBalance</p>
     *
     * @return
     * @description 获取钱包余额
     */
    private BigInteger getBalance() {
        EthGetBalance ethGetBalance = null;
        BigInteger balance = new BigInteger("0");
        try {
            ethGetBalance = web3j.ethGetBalance(
                    address, new DefaultBlockParameter() {
                        @Override
                        public String getValue() {
                            return "latest";
                        }
                    }).send();
            String balanceTxt = ethGetBalance.getResult();
            balance = new BigInteger(Settings.hexString2Bytes(balanceTxt.substring(2)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * <p>getTransactionList</p>
     *
     * @description 获取交易记录
     */
    private BigInteger getTransactionList() {
        BigInteger count = new BigInteger("0");
        try {
            EthGetTransactionCount ethCount = web3j.ethGetTransactionCount(address, new DefaultBlockParameter() {
                @Override
                public String getValue() {
                    return "latest";
                }
            }).send();
            count = ethCount.getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
}
