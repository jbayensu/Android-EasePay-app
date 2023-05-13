package com.droid.omega.easepay;


import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentInfoFragment extends Fragment {

    private TextView orderNumber;
    private TextView amountTxtv;
    private TextView walletBalance;
    private EditText secretCode;
    private String vendorId;
    private String merchantId;
    private String merchantAPI;
    private String userId;
    Bundle bundle;
    private String amountToPay;
    private String walletAmount;
    private PaymentTransactionActivity paymentTransactionActivity;
    private BigDecimal bd;
    private BigDecimal bd2;
    private RequestQueue requestQueue, rq;
    private StringRequest stringRequest, sr;
    private String paymentTransactionUrl = "http://192.168.202.3:82/EasePay/PaymentTransaction.php";

    public PaymentInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_info, container, false);
        amountTxtv = (TextView) view.findViewById(R.id.amount_txtv);
        walletBalance = (TextView) view.findViewById(R.id.wallet_ballance_txtv);
        orderNumber = (TextView)view.findViewById(R.id.orderNum_txtv);
        secretCode = (EditText) view.findViewById(R.id.secretPin);
        Button cancelBtn = (Button)view.findViewById(R.id.cancel_btn);
        Button payBtn = (Button)view.findViewById(R.id.pay_btn);
        //payBtn.setEnabled(false);

        bundle = getArguments();
        userId = bundle.getString("User Id");
        merchantId = bundle.getString("Merchant Id");
        orderNumber.setText(bundle.getString("order Id"));
        amountToPay = bundle.getString("Amount to pay");
        amountTxtv.setText("Ghc "+amountToPay);
        vendorId = bundle.getString("Merchant UserId");
        walletAmount = bundle.getString("Wallet amount");
        walletBalance.setText("Ghc "+ walletAmount);
        merchantAPI = bundle.getString("Mershant's api");

        bd = new BigDecimal(walletAmount);
        bd2 = new BigDecimal(amountToPay);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());

        if(bd2.compareTo(bd) > 0){
            walletBalance.setTextColor(Color.RED);
        }else if(bd2.compareTo(bd) == 0){
            walletBalance.setTextColor(Color.YELLOW);
        }else {
            walletBalance.setTextColor(Color.GREEN);
            if(secretCode.getText().length()>0) {
                payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bd2.compareTo(bd) > 0) {
                            Toast.makeText(getContext(), "insufficient fund", Toast.LENGTH_SHORT).show();
                        } else {
                            rq = Volley.newRequestQueue(getContext());
                            sr = new StringRequest(Request.Method.POST, merchantAPI, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.names().get(0).equals("error")) {
                                            Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                            transferPayment();
                                        }
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("payerId", userId);
                                    hashMap.put("orderId", orderNumber.getText().toString());
                                    return hashMap;
                                }
                            };
                            rq.add(sr);


                        }

                    }
                });
            }else {
                Toast.makeText(getContext(), "Please enter your secret pin code...", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    public void transferPayment(){

        stringRequest = new StringRequest(Request.Method.POST, paymentTransactionUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userID", userId);
                hashMap.put("amount", amountToPay);
                hashMap.put("recipientID", vendorId);
                hashMap.put("pin", secretCode.getText().toString());
                hashMap.put("orderId", orderNumber.getText().toString());
                hashMap.put("merchantId", merchantId);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }


}
