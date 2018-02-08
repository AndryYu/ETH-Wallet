package com.linkage.ethsample.util.rx;

import com.linkage.ethsample.interfaces.RxSingleCallback;

import java.math.BigInteger;

import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018\2\8 0008.
 */

public class RxUtil {

    /**
     * <p>SignleAsync</p>
     * @param number
     * @param callback
     */
    public static  void  SignleAsync(BigInteger number, RxSingleCallback callback){
        Single.fromCallable(() -> number)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleSubscriber<BigInteger>() {

                    @Override
                    public void onSuccess(BigInteger value) {
                        callback.success(value);
                    }

                    @Override
                    public void onError(Throwable error) {
                        callback.canceled(error.getMessage());
                    }
                });
    }
}
