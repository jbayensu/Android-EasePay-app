package com.droid.omega.easepay;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class OthersFragment extends Fragment implements View.OnClickListener{
    private static String userID, userEmail, subject, message;
    private Button submitEmail, expectCall;
    private EditText subjectEtxt, messageEtxt, phoneNumber;
    private SettingsActivity settingsActivity;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private final String sendMessageURL = "http://192.168.202.3:82/easepay/SendMessage.php";

    public void setUserID(String id){
        userID = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public OthersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_others, container, false);

        //Receiving userID
        //userID = getArguments().getString("UserID");

        //Receive userEmail and userID
        settingsActivity = new SettingsActivity();
        userEmail = settingsActivity.getUserEmail();

        submitEmail = (Button) view.findViewById(R.id.submit_email);
        expectCall = (Button) view.findViewById(R.id.expect_call);

        subjectEtxt = (EditText) view.findViewById(R.id.subject);
        messageEtxt = (EditText) view.findViewById(R.id.message);
        phoneNumber = (EditText) view.findViewById(R.id.phone_number);

        submitEmail.setOnClickListener(this);
        expectCall.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == submitEmail){
            subject = subjectEtxt.getText().toString();
            message = messageEtxt.getText().toString();
            sendMessage();
        }else if(v == expectCall){
            Toast.makeText(getContext(),"Feature coming soon...", Toast.LENGTH_SHORT).show();
        }

    }

    protected void sendMessage(){
        final ProgressDialog sending = ProgressDialog.show(getContext(),"Sending message...","Please wait...",false,false);

        stringRequest = new StringRequest(Request.Method.POST, sendMessageURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sending.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getContext(),jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        subjectEtxt.setText("");
                        messageEtxt.setText("");
                    }else{
                        Toast.makeText(getContext(),jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sending.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", userEmail);
                hashMap.put("subject", subject);
                hashMap.put("message", message);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);

    }



}
