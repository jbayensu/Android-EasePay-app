package com.droid.omega.easepay.DataClasses;

/**
 * Created by gleam on 2/20/2016.
 */
public class Merchants {
    private String MerchantId;
    private String MerchantName;
    private String imageName;
    private String paymentType;
    private String MerchantUserID;

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getMerchantUserID() {
        return MerchantUserID;
    }

    public void setMerchantUserID(String merchantID) {
        MerchantUserID = merchantID;
    }



    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    private String apiUrl;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getMerchantName() {
        return MerchantName;
    }

    public void setMerchantName(String merchantName) {
        MerchantName = merchantName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
