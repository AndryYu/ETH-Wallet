package com.linkage.ethsample.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.linkage.ethsample.MainActivity;
import com.linkage.ethsample.R;
import com.linkage.ethsample.bean.FullWallet;
import com.linkage.ethsample.util.AddressNameConverter;
import com.linkage.ethsample.util.Blockies;
import com.linkage.ethsample.util.OwnWalletUtils;
import com.linkage.ethsample.util.Settings;
import com.linkage.ethsample.util.WalletStorage;

import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by Administrator on 2018\2\7 0007.
 */

public class WalletGenService extends IntentService {

    private NotificationCompat.Builder builder;
    final int mNotificationId = 152;

    public WalletGenService() {
        super("WalletGen Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String password = intent.getStringExtra("PASSWORD");

        sendNotification();

        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();

            String walletAddress = OwnWalletUtils.generateWalletFile(password, ecKeyPair, new File(this.getFilesDir(), ""), true);
            //KeyStore keyStore = new KeyStore(new File(this.getFilesDir(), "").getAbsolutePath(), Geth.StandardScryptN, Geth.StandardScryptP);
            //String walletAddress = keyStore.newAccount(password).getAddress().getHex().toLowerCase();
            WalletStorage.getInstance(this).add(new FullWallet("0x" + walletAddress, walletAddress), this);
            AddressNameConverter.getInstance(this).put("0x" + walletAddress, "Wallet " + ("0x" + walletAddress).substring(0, 6), this);
            Settings.walletBeingGenerated = false;

            finished("0x" + walletAddress);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification() {
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(0x2d435c)
                .setTicker(getString(R.string.notification_wallgen_title) )
                .setContentTitle(this.getResources().getString( R.string.wallet_gen_service_title))
                .setOngoing(true)
                .setProgress(0, 0, true)
                .setContentText(getString(R.string.notification_wallgen_maytake));
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void finished(String address) {
        builder
                .setContentTitle(getString(R.string.notification_wallgen_finished))
                .setLargeIcon(Blockies.createIcon(address.toLowerCase()))
                .setAutoCancel(true)
                .setLights(Color.CYAN, 3000, 3000)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText(getString(R.string.notification_click_to_view));

        if (android.os.Build.VERSION.SDK_INT >= 18) // Android bug in 4.2, just disable it for everyone then...
            builder.setVibrate(new long[]{1000, 1000});

        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("STARTAT", 1);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }
}
