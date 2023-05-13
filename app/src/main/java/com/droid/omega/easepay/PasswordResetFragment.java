package com.droid.omega.easepay;


import android.content.Intent;
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
public class PasswordResetFragment extends Fragment implements View.OnClickListener{

    private EditText resetPassword;
    private EditText resetPassword2;
    private Button submitResetPassword;
    private String userId;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private String passwordResetUrl = "http://192.168.202.3:82/EasePay/PasswordReset.php";
    private Bundle bundle;

    public PasswordResetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);

        resetPassword = (EditText) view.findViewById(R.id.new_resetPassword);
        resetPassword2 = (EditText) view.findViewById(R.id.new_resetPassword2);
        submitResetPassword = (Button) view.findViewById(R.id.submit_resetPassword_btn);

        bundle = getArguments();
        userId = bundle.getString("User Id");

        submitResetPassword.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {

        stringRequest = new StringRequest(Request.Method.POST, passwordResetUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("error")){
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
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
                hashMap.put("userId", userId);
                hashMap.put("password", resetPassword.getText().toString());
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);

    }
}
