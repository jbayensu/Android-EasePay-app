package com.droid.omega.easepay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
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
import com.droid.omega.easepay.DataClasses.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Initializing variables
    private EditText inputEmail;
    private EditText inputPassword;
    private TextView forgotPassword;
    private String userID;
    private String fullName;
    private static String email;
    private RequestQueue requestQueue;
    private StringRequest request;
    private String errorMsg;
    private Intent i;
    private UserDetails userDetails;
    private static final String url = "http://192.168.202.3:82/EasePay/userLogin.php";
    String ipa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotPassword = (TextView) findViewById(R.id.ForgottenPwrdTxv);
        inputEmail = (EditText) findViewById(R.id.user_email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button SignInBtn = (Button) findViewById(R.id.btn_signin);
        Button SignUpBtn = (Button) findViewById(R.id.btn_signup);

        forgotPassword.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);

        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting a new Intent
                boolean check = submitForm();
                email = inputEmail.getText().toString();
                if(check){
                    new checkConnnection().isConnectedToServer(url,10);
                    request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.names().get(0).equals("success")) {
                                    userID = jsonObject.getString("userID");
                                    fullName = jsonObject.getString("user names");
                                    userDetails.setUserID(userID);
                                    userDetails.setUserEmail(email);

                                    if (jsonObject.names().get(3).equals("unverified")) {
                                        i = new Intent(getApplicationContext(),VerificationPinActivity.class);
                                        i.putExtra("user id", userID);
                                        i.putExtra("full name", fullName);
                                        i.putExtra("email", email);
                                        startActivity(i);
                                        inputEmail.setText("");
                                        inputPassword.setText("");
                                    }else {
                                        if(jsonObject.names().get(4).equals("active")){
                                            Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                            //Starting a new Intent
                                            i = new Intent(getApplicationContext(), MainActivity.class);
                                            //Sending Data to another activity
                                            i.putExtra("user id", userID);
                                            i.putExtra("full name", fullName);
                                            i.putExtra("email", email);
                                            startActivity(i);
                                            inputEmail.setText("");
                                            inputPassword.setText("");
                                        }else {
                                            showError("Sorry, this account has been deactivated due to some suspicious activities this account, please Call the following number for assistance: 0243742166");
                                        }
                                    }
                                } else {
                                    errorMsg = "ERROR " + jsonObject.getString("error");

                                    showError(errorMsg);
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
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("email", email);
                            hashMap.put("password", inputPassword.getText().toString());
                            return hashMap;
                        }
                    };
                    requestQueue.add(request);
                }

            }
        });

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    /*
    * * Validating form
    */
    private boolean submitForm() {
        if (!validateEmail()) {
            return false;
        }

        if (!validatePassword()) {
            return false;
        }

        return true;

    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.length() ==0 || !isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), "Enter your correct Email", Toast.LENGTH_LONG).show();
            requestFocus(inputEmail);
            return false;
        } else {
            return true;
        }


    }

    private boolean validatePassword() {
        if (inputPassword.getText().length()==0) {
            Toast.makeText(getApplicationContext(), "Enter your Password", Toast.LENGTH_LONG).show();
            requestFocus(inputPassword);
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

    private class checkConnnection extends AsyncTask<URL, Integer, Long> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(LoginActivity.this, "Authenticating...", null, true, true);
        }

        @Override
        protected void onPostExecute(Long l) {
            super.onPostExecute(l);
            loading.dismiss();
        }

        @Override
        protected Long doInBackground(URL... urls) {
            if(isConnectedToServer(url, 10)){
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Url Connection Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        public boolean isConnectedToServer(String url, int timeout) {
            try{

                URL myUrl = new URL(url);

                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(timeout);
                connection.connect();
                Toast.makeText(getApplicationContext(),"valid url " + url , Toast.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"invalid url " + url, Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), ForgottenPasswordActivity.class));
    }

    /*public String getIpAddress() {
        String ip = "";
        try{
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()){
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    String ipAddress = "";
                    if(inetAddress.isLoopbackAddress()){
                        ipAddress = "LoopbackAddress";
                    }else if(inetAddress.isSiteLocalAddress()){
                        ipAddress = "isSiteLocalAddress";
                    }else if(inetAddress.isLinkLocalAddress()){
                        ipAddress = "isLinkLocalAddress";
                    }else if(inetAddress.isMulticastAddress()){
                        ipAddress = "isMulticastAddress";
                    }
                    ip += ipAddress + inetAddress.getHostAddress()+ "\n";
                }
            }

        }catch (SocketException ex){
            ex.printStackTrace();
        }

        return ip;
    }*/

}
