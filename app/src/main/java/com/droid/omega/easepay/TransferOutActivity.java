package com.droid.omega.easepay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class TransferOutActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private String userID;
    private String amount;
    private TextView balance;
    private EditText amountToSend;
    private EditText recipientId;
    private EditText pin;
    private Button send;
    private BigDecimal bd;
    private BigDecimal bd2;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private String walletUrl = "http://192.168.202.3:82/EasePay/walletBallance.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_out);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Send Money");

        intent = getIntent();
        userID =intent.getStringExtra("userID");
        amount =intent.getStringExtra("amount");

        balance = (TextView) findViewById(R.id.balance);
        amountToSend = (EditText) findViewById(R.id.amount_out);
        recipientId = (EditText) findViewById(R.id.userId_out);
        pin = (EditText) findViewById(R.id.userPin);
        send = (Button) findViewById(R.id.transfer_out_btn);

        send.setOnClickListener(this);

        balance.setText(amount);



    }

    @Override
    public void onClick(View v) {
        //final ProgressDialog sending = ProgressDialog.show(getApplicationContext(),"Transfering...","Please wait...",false,false);
        if(recipientId.getText().equals(userID)){
            Toast.makeText(getApplicationContext(), "you can not send money to yourself", Toast.LENGTH_SHORT).show();
        }else {
        if(recipientId.length()>0 && amountToSend.length()>0 && pin.length()>0) {
            bd = new BigDecimal(amount);
            bd2 = new BigDecimal(amountToSend.getText().toString());

            if (bd2.compareTo(bd) > 0) {
                Toast.makeText(getApplicationContext(), "insufficient fund", Toast.LENGTH_SHORT).show();
            } else {
                bd = bd.subtract(bd2);
                bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest = new StringRequest(Request.Method.POST, walletUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //sending.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.names().get(0).equals("success")) {
                                balance.setText(bd.toString());
                                WalletFragment walletf = new WalletFragment();
                                walletf.setAmount(bd.toString());
                                walletf.walletBalance.setText(bd.toString());
                                Toast.makeText(getApplicationContext(), "transfer successfull", Toast.LENGTH_SHORT).show();
                                amountToSend.setText("");
                                recipientId.setText("");
                                pin.setText("");
                            } else {
                                showError(jsonObject.getString("error") + ", Please try again later.");
                                amountToSend.setText("");
                                recipientId.setText("");
                                pin.setText("");
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //sending.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("userID", userID);
                        hashMap.put("amount", amountToSend.getText().toString());
                        hashMap.put("recipientID", recipientId.getText().toString());
                        hashMap.put("pin", pin.getText().toString());
                        return hashMap;
                    }
                };
                requestQueue.add(stringRequest);


            }
        }else {
            showError("Please, Fill all fields");
        }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    public void showError(String Msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Msg).setTitle("EasePay").setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
