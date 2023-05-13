package com.droid.omega.easepay;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.droid.omega.easepay.DataClasses.AdvancedWebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private ImageView wallet, merchant, history, contact;
    private Intent intent;
    private Bundle bundle, b;
    private String userID, email, fullNames;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        wallet = (ImageView) view.findViewById(R.id.wallet);
        merchant= (ImageView) view.findViewById(R.id.merchant);
        history = (ImageView) view.findViewById(R.id.history);
        contact = (ImageView) view.findViewById(R.id.contact);

        b = getArguments();
        userID = b.getString("userID");
        email = b.getString("email");
        fullNames = b.getString("full name");

        wallet.setOnClickListener(this);
        merchant.setOnClickListener(this);
        history.setOnClickListener(this);
        contact.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if(v == wallet){
            Toast.makeText(getContext(), "wallet clicked", Toast.LENGTH_SHORT).show();
            fragment = new WalletFragment();
            bundle = new Bundle();
            bundle.putString("userID", userID);
        }else if(v == merchant){
            Toast.makeText(getContext(), "merchant clicked", Toast.LENGTH_SHORT).show();
            fragment = new ListMerchantFragment();
            bundle = new Bundle();
            bundle.putString("userID", userID);
            fragment.setArguments(bundle);
        }else if(v == history){
            Toast.makeText(getContext(), "history clicked", Toast.LENGTH_SHORT).show();
            fragment = new ListTransactionHistoryFragment();
            bundle = new Bundle();
            bundle.putString("userID", userID);
            fragment.setArguments(bundle);
        }else if(v == contact){
            Toast.makeText(getContext(), "contact clicked", Toast.LENGTH_SHORT).show();
            intent = new Intent(getContext(), SettingsActivity.class);
            intent.putExtra("page", 2);
            intent.putExtra("email", email);
            intent.putExtra("user id", userID);
            intent.putExtra("full name", fullNames);
            startActivity(intent);
            getActivity().finish();
        }

        if (fragment != null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }


    }
}
