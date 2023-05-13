package com.droid.omega.easepay;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class AccountFragment extends Fragment implements View.OnClickListener{

    private static final int RESULT_LOAD_IMAGE = 1;
    private final String SERVER_ADDRESS = "http://192.168.202.3:82/EasePay/";
    private final String updatePasswordURL = "http://192.168.202.3:82/easepay/UpdateUserPassword.php";
    private final String updateProfileURL = "http://192.168.202.3:82/easepay/UpdateUserNames.php";
    private static String userID;
    private static String fullName;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    String mnameInit;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;


    private Button uploadBtn, submitProfilBtn, submitPasswordBtn;
    private ImageView profileImageView;
    private EditText fnameEtxt, mnameEtxt, lnameEtxt, oldPasswordEtxt, newPasswordEtxt, newPaswordEtxt2;
    private Bitmap bitmap;


    public void setUserID(String id){
        userID = id;
    }

    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        uploadBtn = (Button) view.findViewById(R.id.upload_img_btn);
        submitProfilBtn = (Button) view.findViewById(R.id.submit_profile_btn);
        submitPasswordBtn = (Button) view.findViewById(R.id.submit_password_btn);

        profileImageView = (ImageView) view.findViewById(R.id.profile_img);

        fnameEtxt = (EditText) view.findViewById(R.id.profile_fname);
        mnameEtxt = (EditText) view.findViewById(R.id.profile_mname);
        lnameEtxt = (EditText) view.findViewById(R.id.profile_lname);
        oldPasswordEtxt = (EditText) view.findViewById(R.id.old_password);
        newPasswordEtxt = (EditText) view.findViewById(R.id.new_password);
        newPaswordEtxt2 = (EditText) view.findViewById(R.id.new_password2);

        uploadBtn.setOnClickListener(this);
        submitProfilBtn.setOnClickListener(this);
        submitPasswordBtn.setOnClickListener(this);
        profileImageView.setOnClickListener(this);



        requestQueue = Volley.newRequestQueue(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        String oldPass = oldPasswordEtxt.getText().toString();
        String newPass = newPasswordEtxt.getText().toString();
        String newPass2 = newPaswordEtxt2.getText().toString();

        if(v == uploadBtn){
            uploadImage();
        }else if(v == submitProfilBtn){
            updateProfile();
        }else if(v == submitPasswordBtn){
            if(newPass.equals(newPass2)) {
                updatePassword();
            }else{
                Toast.makeText(getContext(), "passwords do not match", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(getContext(), "coming soon...", Toast.LENGTH_SHORT).show();
        }else if (v == profileImageView){
            showFileChooser();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            Uri selectedImage = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                profileImageView.setImageURI(selectedImage);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(),"Uploading...","Please wait...",false,false);
        stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS + "uploadPics.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getContext(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, userID);

                //returning parameters
                return params;
            }
        };
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void updateProfile(){

        final ProgressDialog Updating = ProgressDialog.show(getContext(),"Updating...","Please wait...",false,false);
        stringRequest = new StringRequest(Request.Method.POST, updateProfileURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Updating.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();

                        if(mnameEtxt.getText().toString() != ""){
                            mnameInit = mnameEtxt.getText().toString().substring(0,1) + ".";
                        }
                        fullName = fnameEtxt.getText().toString() +" "+ mnameInit +" "+ lnameEtxt.getText().toString() ;
                        SettingsActivity settingsActivity = new SettingsActivity();
                        settingsActivity.setFullName(fullName);
                        fnameEtxt.setText("");
                        mnameEtxt.setText("");
                        lnameEtxt.setText("");
                    }else {
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException ex){
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Updating.dismiss();
                //Showing toast
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", userID);
                hashMap.put("fname", fnameEtxt.getText().toString());
                hashMap.put("mname", mnameEtxt.getText().toString());
                hashMap.put("lname", lnameEtxt.getText().toString());
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updatePassword(){
        final ProgressDialog Updating = ProgressDialog.show(getContext(),"Updating...","Please wait...",false,false);
        stringRequest = new StringRequest(Request.Method.POST, updatePasswordURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Updating.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("error")){
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        oldPasswordEtxt.setText("");
                        newPasswordEtxt.setText("");
                        newPaswordEtxt2.setText("");
                    }
                } catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Updating.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("id", userID);
                hashMap.put("password", newPasswordEtxt.getText().toString());
                hashMap.put("oldPassword", oldPasswordEtxt.getText().toString());
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }
}
