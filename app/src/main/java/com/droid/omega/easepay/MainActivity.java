package com.droid.omega.easepay;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.droid.omega.easepay.DataClasses.AdvancedWebView;
import com.droid.omega.easepay.DataClasses.UserDetails;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private ImageView userImage;
    private Intent intent;
    private Bundle bundle;
    private static String userID;
    private static String fullNames;
    private static String email;
    private static final String SERVER_URL = "http://192.168.202.3:82/EasePay/images/";
    private static final   String url = "http://192.168.202.3:82/easepay/userLogout.php";
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private FragmentManager fm;
    private Fragment fragment;

    public String getUserID(){return userID;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new HomeFragment();
            bundle = new Bundle();
            bundle.putString("userID", userID);
            bundle.putString("email", userID);
            bundle.putString("full name", userID);
            fragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView userName = (TextView) hView.findViewById(R.id.user_name);
        TextView userEmail = (TextView) hView.findViewById(R.id.user_email);



        userImage = (ImageView) hView.findViewById(R.id.user_image);


        //Receiving the data
        intent = getIntent();
        userID = intent.getStringExtra("user id");
        fullNames = intent.getStringExtra("full name");
        email = intent.getStringExtra("email");
        //Loading info from url
        //loadUserInfo();

        //Display info
        loadImage();
        userName.setText(fullNames + " (" + userID + ")" );
        userEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                Fragment fragment = null;

                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    fragment = new HomeFragment();
                    bundle = new Bundle();
                    bundle.putString("userID", userID);
                    bundle.putString("email", userID);
                    bundle.putString("full name", userID);
                    fragment.setArguments(bundle);
                    getSupportActionBar().setTitle("EasePay");
                    // Handle the preference  action
                } else if (id == R.id.nav_wallet) {
                    // Handle the About action
                    fragment = new WalletFragment();
                    bundle = new Bundle();
                    bundle.putString("userID", userID);
                    getSupportActionBar().setTitle("Wallet");
                } else if (id == R.id.nav_merchants) {
                    // Handle the About action
                    fragment = new ListMerchantFragment();
                    bundle = new Bundle();
                    bundle.putString("userID", userID);
                    fragment.setArguments(bundle);
                    getSupportActionBar().setTitle("Merchants");
                } else if (id == R.id.nav_history) {
                    // Handle the About action
                    fragment = new ListTransactionHistoryFragment();
                    bundle = new Bundle();
                    bundle.putString("userID", userID);
                    fragment.setArguments(bundle);
                    getSupportActionBar().setTitle("History");
                } else if (id == R.id.nav_logout) {
                    // Handle the About action

                    requestQueue = Volley.newRequestQueue(getApplicationContext());

                    stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.names().get(0).equals("success")) {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex){
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
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("id", userID);
                            return hashMap;
                        }
                    };requestQueue.add(stringRequest);

                    finish();

                } else if (id == R.id.nav_preferences) {
                    // Handle the About action
                   intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("user id", userID);
                    intent.putExtra("full name", fullNames);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.nav_contact_us) {
                    // Handle the About action
                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent.putExtra("page", 2);
                    intent.putExtra("email", email);
                    intent.putExtra("user id", userID);
                    intent.putExtra("full name", fullNames);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.nav_about) {
                    // Handle the About action
                    DialogFragment newDialogF = new AboutFragment();
                    newDialogF.show(getSupportFragmentManager(),"About Us");
                }

                if (fragment != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void loadImage(){
        Picasso.with(getApplicationContext()).invalidate(SERVER_URL + userID+".JPG");
        Picasso.with(getApplicationContext())
                .load(SERVER_URL + userID+".JPG")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_account_circle_white_48dp)// optional
                .error(R.drawable.ic_account_circle_black_48dp)// optional
                .into(userImage);
    }


}
