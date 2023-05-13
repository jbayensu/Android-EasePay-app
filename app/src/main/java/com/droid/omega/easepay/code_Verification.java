package com.droid.omega.easepay;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class code_Verification extends Fragment implements View.OnClickListener {

    private TextView merchantName;
    private EditText orderNumber;
    private Button verifyBtn;
    private PaymentTransactionActivity paymentTransactionActivity;
    private String merchantApiUrl;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private String walletUrl = "http://192.168.202.3:82/EasePay/walletBallance.php";
    private String orderAmount;
    private String walletAmount;
    private String mUserId;
    private String mId;
    private String PayerId;

    public code_Verification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_code__verification, container, false);

        merchantName = (TextView) view.findViewById(R.id.merchant_name);
        orderNumber = (EditText) view.findViewById(R.id.order_number);
        verifyBtn = (Button) view.findViewById(R.id.verify_btn);

        paymentTransactionActivity = new PaymentTransactionActivity();
        mId = paymentTransactionActivity.getMerchantId();
        merchantName.setText(paymentTransactionActivity.getMerchantName());
        merchantApiUrl = paymentTransactionActivity.getMerchantApiUrl();
        mUserId = paymentTransactionActivity.getMerchantUserId();
        PayerId = paymentTransactionActivity.getUserID();

        verifyBtn.setOnClickListener(this);
        requestQueue = Volley.newRequestQueue(getContext());

        stringRequest = new StringRequest(Request.Method.POST, walletUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("error")){
                        walletAmount = "0.00";
                        Toast.makeText(getContext(),jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }else{
                        walletAmount = jsonObject.getString("amount");
                    }
                }catch (JSONException ex){
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
                hashMap.put("userid", paymentTransactionActivity.getUserID());
                return hashMap;
            }
        };requestQueue.add(stringRequest);


        return view;
    }

    @Override
    public void onClick(View v) {
        if(!orderNumber.getText().toString().isEmpty()) {
            new checkConnnection().isConnectedToServer(merchantApiUrl,10);
            stringRequest = new StringRequest(Request.Method.POST, merchantApiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.names().get(0).equals("error")){

                            Toast.makeText(getContext(),jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }else{
                            orderAmount = jsonObject.getString("totalAmount");
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment codeFrag = new PaymentInfoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("User Id", PayerId);
                            bundle.putString("Merchant Id", mId);
                            bundle.putString("order Id", orderNumber.getText().toString());
                            bundle.putString("Amount to pay", orderAmount);
                            bundle.putString("Merchant UserId", mUserId);
                            bundle.putString("Wallet amount", walletAmount);
                            bundle.putString("Mershant's api", merchantApiUrl);
                            codeFrag.setArguments(bundle);
                            ft.replace(R.id.payment_fragmentContainer, codeFrag);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }catch (JSONException ex){
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
                hashMap.put("orderId",orderNumber.getText().toString());
                return hashMap;
            }

            };requestQueue.add(stringRequest);

        }else{
            Toast.makeText(getContext(), "Please Enter the Order Number in the field above", Toast.LENGTH_SHORT).show();
        }

    }


    private class checkConnnection extends AsyncTask<URL, Integer, Long> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getContext(), "Authenticating...", null, true, true);
        }

        @Override
        protected void onPostExecute(Long l) {
            super.onPostExecute(l);
            loading.dismiss();
        }

        @Override
        protected Long doInBackground(URL... urls) {
            if(isConnectedToServer(merchantApiUrl, 10)){
                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(), "Url Connection Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        public boolean isConnectedToServer(String url, int timeout) {
            try{

                URL myUrl = new URL(url);

                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(timeout);
                connection.connect();
                Toast.makeText(getContext(),"valid url " + url , Toast.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(),"invalid url " + url, Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }
}
