package com.linkage.ethsample.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.linkage.ethsample.MainActivity;
import com.linkage.ethsample.R;
import com.linkage.ethsample.util.WalletStorage;

import org.json.JSONException;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018\2\7 0007.
 */

public class TransactionService extends IntentService {

    private NotificationCompat.Builder builder;
    final int mNotificationId = 153;

    public TransactionService() {
        super("Transaction Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sendNotification();
        String password = intent.getStringExtra("PASSWORD");
        String nodeaddress = intent.getStringExtra("NODEADDRESS");
        String toaddress = intent.getStringExtra("TOADDRESS");
        String fromAddress = intent.getStringExtra("FROMADDRESS");
        int amount = intent.getIntExtra("AMOUNT", 1);



        try {
            Web3j web3j = Web3jFactory.build(new HttpService(nodeaddress));
            /*EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
            BigInteger gasPrice = ethGasPrice.getGasPrice();*/
            WalletStorage instance = WalletStorage.getInstance(getApplicationContext());
            Credentials credentials = instance.getFullWallet(getApplicationContext(), password, fromAddress);

            TransactionReceipt transferReceipt = Transfer.sendFunds(
                    web3j,
                    credentials,
                    toaddress,  // you can put any address here
                    new BigDecimal(amount), Convert.Unit.ETHER);  // 1 wei = 10^-18 Ether;

            suc("");
        } catch (IOException e) {
            e.printStackTrace();
            error(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            error(e.getMessage());
        } catch (CipherException e) {
            e.printStackTrace();
            error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage());
        }
    }

    private void suc(String hash) {
        builder
                .setContentTitle(getString(R.string.notification_transfersuc))
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText("");

        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("STARTAT", 2);
        main.putExtra("TXHASH", hash);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void error(String err) {
        builder
                .setContentTitle(getString(R.string.notification_transferfail))
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText(err);

        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("STARTAT", 2);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void sendNotification() {
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(0x2d435c)
                .setTicker(getString(R.string.notification_transferingticker))
                .setContentTitle(getString(R.string.notification_transfering_title))
                .setContentText(getString(R.string.notification_might_take_a_minute))
                .setOngoing(true)
                .setProgress(0, 0, true);
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }
}
