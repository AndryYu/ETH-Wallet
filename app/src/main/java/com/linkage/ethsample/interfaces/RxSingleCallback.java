package com.linkage.ethsample.interfaces;

import java.math.BigInteger;

/**
 * Created by Administrator on 2018\2\8 0008.
 */

public interface RxSingleCallback {

    void success(BigInteger integer);

    void canceled(String error);
}
