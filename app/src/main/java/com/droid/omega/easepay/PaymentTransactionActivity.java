package com.droid.omega.easepay;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
public class PaymentTransactionActivity extends AppCompatActivity {

    //private Intent receivedData;
    private static String userID;
    private static String OrderId;
    private static String merchantId;
    private static String merchantName;
    private static String merchantApiUrl;
    private static String merchantUserId;
    private Intent intent;
    private String amount;

    public String getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(String merchantUserId) {
        PaymentTransactionActivity.merchantUserId = merchantUserId;
    }



    /*public String getAmount() {
        return amount;
    }*/

    /*public void setAmount(String amount) {
        this.amount = amount;
    }*/



    //private static String userEmail;

    /*public static String getUserEmail(){
        return userEmail;
    }

    public static void setUserEmail(String email){ userEmail = email;}*/
    //public String getOrderId(){return OrderId;}
    //public void setOrderId(String orderId){OrderId = orderId; }
    public void closeMe(){finish();}
    public String getMerchantApiUrl(){return merchantApiUrl;}
    public void setMerchantApiUrl(String apiUrl){merchantApiUrl = apiUrl;}
    public void setMerchantName(String name){merchantName = name;}
    public String getMerchantName(){return merchantName;}
    public void setUserID(String id){userID = id;}
    public String getUserID(){return userID;}
    public String getMerchantId() {return merchantId;}
    public void setMerchantId(String merchantId) {PaymentTransactionActivity.merchantId = merchantId;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_transaction);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Transaction");

        intent = getIntent();
        setMerchantId(intent.getStringExtra("Merchant Id"));
        setMerchantName(intent.getStringExtra("Merchant Name"));
        setUserID(intent.getStringExtra("User Id"));
        setMerchantApiUrl(intent.getStringExtra("Merchant Api Url"));
        setMerchantUserId(intent.getStringExtra("Merchant UserId"));


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.payment_fragmentContainer);

        if (fragment == null) {
            fragment = new code_Verification();
            fm.beginTransaction()
                    .add(R.id.payment_fragmentContainer, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
