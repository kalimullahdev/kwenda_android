package com.kwendaapp.rideo.PaytmGateway;

import com.google.gson.annotations.SerializedName;

public class Checksum {

    @SerializedName("CHECKSUMHASH")
    private final String checksumHash;

    @SerializedName("ORDER_ID")
    private final String orderId;

    @SerializedName("payt_STATUS")
    private final String paytStatus;
    public static final String TAG = Checksum.class.getSimpleName();

    public Checksum(String checksumHash, String orderId, String paytStatus) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;
        this.paytStatus = paytStatus;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaytStatus() {
        return paytStatus;
    }
}