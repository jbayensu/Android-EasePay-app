package com.droid.omega.easepay;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.droid.omega.easepay.DataClasses.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by gleam on 2/20/2016.
 */
public class WalletFragment extends Fragment {
    private Button cashInBtn;
    private Button cashOutBtn;
    private TextView walletDate;
    public static TextView walletBalance;
    private String userId;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private MainActivity mainActivity;
    private Intent intent;
    private String amount;
    private String walletUrl = "http://192.168.202.3:82/EasePay/walletBallance.php";
    private CharSequence cashOutOptions[] = new CharSequence[] {"Send Money to another user", "Send Money to Bank account"};

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        // Inflate the layout for this fragment

        cashInBtn = (Button) view.findViewById(R.id.cash_in_btn);
        cashOutBtn = (Button) view.findViewById(R.id.cash_out_btn);
        walletDate = (TextView) view.findViewById(R.id.wallet_Date);
        walletBalance = (TextView) view.findViewById(R.id.wallet_balance);

        String currentDateTime = DateFormat.getDateInstance().format(new Date());
        walletDate.setText(currentDateTime);

        mainActivity = new MainActivity();
        userId = mainActivity.getUserID();


        requestQueue = Volley.newRequestQueue(getContext());
        stringRequest = new StringRequest(Request.Method.POST, walletUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("error")){
                        walletBalance.setText("Ghc 0.00");
                        //Toast.makeText(getContext(),jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }else{
                        amount = jsonObject.getString("amount");

                        walletBalance.setText("Ghc " + amount);
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
                hashMap.put("userid", userId);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);

        cashInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Cash-in Service not Available yet", Toast.LENGTH_SHORT).show();
            }
        });

        cashOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose and option");
                builder.setItems(cashOutOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (cashOutOptions[which] == "Send Money to another user") {
                            intent = new Intent(getContext(), TransferOutActivity.class);
                            intent.putExtra("userID", userId);
                            intent.putExtra("amount", amount);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Cash-out Service not Available yet", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                builder.show();
            }
        });

        return view;

    }

}
