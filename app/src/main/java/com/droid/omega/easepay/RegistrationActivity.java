package com.droid.omega.easepay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {

    private EditText inputFName;
    private EditText inputMName;
    private EditText inputLName;
    private EditText inputEmail1;
    private EditText inputPassword1;
    private EditText inputTel;
    private String fName;
    private String mName;
    private String lName;
    private String email;
    private String password;
    private String tel;
    private static String pinCode;
    private RequestQueue requestQueue;
    private static final String URL = "http://192.168.202.3:82/EasePay/UserRegistration.php";
    private StringRequest request;
    private Intent i;
    private Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");

        inputFName = (EditText) findViewById(R.id.user_fname);
        inputMName = (EditText) findViewById(R.id.user_mname);
        inputLName = (EditText) findViewById(R.id.user_lname);
        inputEmail1 = (EditText) findViewById(R.id.user_email1);
        inputPassword1 = (EditText) findViewById(R.id.password1);
        inputTel = (EditText) findViewById(R.id.user_tel);
        Button btnSubmit = (Button) findViewById(R.id.btn_submit);


        r = new Random();
        pinCode = Integer.toString(100000 + r.nextInt(900000));
        requestQueue = Volley.newRequestQueue(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                boolean check = submitForm1();

                fName = inputFName.getText().toString();
                mName = inputMName.getText().toString();
                lName = inputLName.getText().toString();
                email = inputEmail1.getText().toString();
                password = inputPassword1.getText().toString();
                tel = inputTel.getText().toString();
                if (check) {

                    WifiManager wifiOn = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    if (wifiOn.isWifiEnabled()) {

                        BroadcastReceiver broadcastReceiver = new WifiBroadcastReceiver();
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
                        registerReceiver(broadcastReceiver, intentFilter);

                        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.names().get(0).equals("success")) {
                                        Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                        //sending message the new user
                                       sendMessage("your pin Code is " + pinCode);
                                        /*try{
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(inputTel.getText().toString(),null,jsonObject.getString("success"),null,null);
                                            Toast.makeText(getApplicationContext(), "sms sent... ", Toast.LENGTH_SHORT).show();

                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Error, sms not sent... ", Toast.LENGTH_SHORT).show();
                                        }*/
                                        inputFName.setText("");
                                        inputMName.setText("");
                                        inputLName.setText("");
                                        inputEmail1.setText("");
                                        inputPassword1.setText("");
                                        inputTel.setText("");
                                        pinCode = "";

                                    } else {
                                        Toast.makeText(getApplicationContext(), "ERROR " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                                hashMap.put("fname", fName);
                                hashMap.put("mname", mName);
                                hashMap.put("lname", lName);
                                hashMap.put("email", email);
                                hashMap.put("password", password);
                                hashMap.put("tel", tel);
                                hashMap.put("pin", pinCode);
                                return hashMap;
                            }
                        };
                        requestQueue.add(request);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please, check wifi connection", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

    /*
   * * Validating form
   */
    private boolean submitForm1() {
        if (!validateFName()) {
            return false;
        }

        if (!validateLName()) {
            return false;
        }

        if (!validateEmail()) {
            return false;
        }

        if (!validatePassword()) {
            return false;
        }

        return true;

    }

    private boolean validateEmail() {
        String email = inputEmail1.getText().toString().trim();

        if (email.length() ==0 || !isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), "Enter your correct Email", Toast.LENGTH_LONG).show();
            requestFocus(inputEmail1);
            return false;
        } else {
            return true;
        }


    }

    private boolean validateFName() {
        if (inputFName.getText().length()==0) {
            Toast.makeText(getApplicationContext(), "Enter your First Name", Toast.LENGTH_LONG).show();
            requestFocus(inputFName);
            return false;
        } else {
            return true;
        }

    }

    private boolean validateLName() {
        if (inputLName.getText().length()==0) {
            Toast.makeText(getApplicationContext(), "Enter your Last Name", Toast.LENGTH_LONG).show();
            requestFocus(inputLName);
            return false;
        } else {
            return true;
        }

    }


    private boolean validatePassword() {
        if (inputPassword1.getText().length()==0) {
            Toast.makeText(getApplicationContext(), "Enter your Password", Toast.LENGTH_LONG).show();
            requestFocus(inputPassword1);
            return false;
        } else {
            return true;
        }

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void sendMessage(String Msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Msg).setTitle("EasePay").setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    public class WifiBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if(SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED){
                    boolean connected = checkConnectedToDesiredWifi();

                }
            }
        }

        private boolean checkConnectedToDesiredWifi(){
            boolean connected = false;

           // String desiredMacAddress =
           // ";

            //WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            //WifiInfo wifi = wifiManager.getConnectionInfo();
            //if (wifi != null) {
              //  String bssid = wifi.getBSSID();
               // connected = desiredMacAddress.equals(bssid);
            //    if (!connected) {
                 //   Toast.makeText(getApplicationContext(), "Please Check to be sure you are connected to the right wifi connection", Toast.LENGTH_SHORT).show();
           //     } else {
                    Toast.makeText(getApplicationContext(), "connection successful", Toast.LENGTH_SHORT).show();
            //    }
           // } else {
            //    Toast.makeText(getApplicationContext(), "Sorry, could not get wifi information", Toast.LENGTH_SHORT).show();
            //}


            return connected;
        }
    }
}
