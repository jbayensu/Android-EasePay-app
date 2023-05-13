package com.droid.omega.easepay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class VerificationPinActivity extends AppCompatActivity {

    private Button submitPin;
    private EditText oldPin;
    private EditText newPin;
    private EditText reNewPin;
    private String userID;
    private String fullName;
    private String email;
    private Intent intent;
    private RequestQueue requestQ;
    private StringRequest strRequest;
    private static final String myUrl = "http://192.168.202.3:82/easepay/UserPinUpdate.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_pin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pin Authentication");


        submitPin = (Button) findViewById(R.id.submit_pin);
        oldPin = (EditText) findViewById(R.id.old_pin);
        newPin = (EditText) findViewById(R.id.new_pin);
        reNewPin = (EditText) findViewById(R.id.re_new_pin);

        intent = getIntent();
        userID = intent.getStringExtra("user id");
        email = intent.getStringExtra("email");
        fullName = intent.getStringExtra("user names");


        submitPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestQ = Volley.newRequestQueue(getApplicationContext());
                strRequest = new StringRequest(Request.Method.POST, myUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                /*Toast.makeText(getApplicationContext(), "SUCCESS" + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(getApplication(), MainActivity.class);
                                mainIntent.putExtra("user id", userID);
                                mainIntent.putExtra("full name", fullName);
                                mainIntent.putExtra("email", email);
                                startActivity(mainIntent);**/
                                finish();
                            } else{
                                Toast.makeText(getApplicationContext(), "ERROR" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String err = error.toString();
                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("id", userID);
                        hashMap.put("pin", newPin.getText().toString());
                        hashMap.put("oldPin", oldPin.getText().toString());
                        return hashMap;
                    }
                };
                requestQ.add(strRequest);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
