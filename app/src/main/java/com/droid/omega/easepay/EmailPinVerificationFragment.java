package com.droid.omega.easepay;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPinVerificationFragment extends Fragment implements View.OnClickListener {

    private EditText emailVerification;
    private EditText pinVerification;
    private Button submitVerification;
    private String email;
    private String pin;
    private String UserId;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private String passwordRequestUrl = "http://192.168.202.3:82/EasePay/PasswordReset.php";


    public EmailPinVerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_email_pin_verification, container, false);

        emailVerification = (EditText) view.findViewById(R.id.email_verification);
        pinVerification = (EditText) view.findViewById(R.id.pin_verification);
        submitVerification = (Button) view.findViewById(R.id.submit_verification);

        email = emailVerification.getText().toString();
        pin = pinVerification.getText().toString();

        submitVerification.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {

        stringRequest = new StringRequest(Request.Method.POST, passwordRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("error")){
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }else{
                        UserId = jsonObject.getString("userId");
                        Toast.makeText(getContext(), "Hello " + UserId, Toast.LENGTH_SHORT).show();
                       FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment pRF = new PasswordResetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("User Id", UserId);
                        pRF.setArguments(bundle);
                        ft.replace(R.id.password_reset_container, pRF);
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
                hashMap.put("email", emailVerification.getText().toString());
                hashMap.put("pin", pinVerification.getText().toString());
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);

    }
}
