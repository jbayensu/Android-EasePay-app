package com.droid.omega.easepay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.droid.omega.easepay.DataClasses.Merchants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gleam on 2/20/2016.
 */
public class ListMerchantFragment extends Fragment {
    //DummyData
   // private String[] merchantNames = {"Hellen's Online Bookshop","Rabi's Online Art Gallery"};
   // private int[] image = {R.drawable.ic_add_shopping_cart_black_48dp,R.drawable.ic_account_balance_wallet_black_48dp};

    private ArrayList<Merchants> merchants;
    private RecyclerView merchantRecyclerView;
    private MerchantAdapter mAdapter;
    private Bundle bundle;
    private Intent merchantIntent;
    private MainActivity mainActivity;
    private final String IMAGE_URL = "http://192.168.202.3:82/EasePay/images/";
    private final String MERCHANT_URL = "http://192.168.202.3:82/EasePay/MerchantList.php/";
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String merchantName;
    private String merchantId;
    private String merchantUserId;
    private String payType;
    private String merchantApiUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final ProgressDialog sending = ProgressDialog.show(getContext(),"Loading Merchants...","Please wait...",false,false);

        merchants = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MERCHANT_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sending.dismiss();
                try{
                    JSONArray merchantList = response.getJSONArray("Merchants List");

                    for(int i=0; i<merchantList.length(); i++){
                        Merchants s = new Merchants();
                        JSONObject merchant = merchantList.getJSONObject(i);
                        merchantName = merchant.getString("name");
                        payType = merchant.getString("paymenttype");
                        merchantApiUrl = merchant.getString("dblink");
                        merchantId = merchant.getString("merchantid");
                        merchantUserId = merchant.getString("userid");

                        s.setMerchantId(merchantId);
                        s.setMerchantName(merchantName);
                        s.setImageName(merchantId);
                        s.setPaymentType(payType);
                        s.setApiUrl(merchantApiUrl);
                        s.setMerchantUserID(merchantUserId);
                        merchants.add(s);
                    }

                }catch (JSONException ex){
                    ex.printStackTrace();
                }
                updateUI();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sending.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_merchants, container, false);

        merchantRecyclerView = (RecyclerView) view.findViewById(R.id.merchant_recycler_view);
        //merchantRecyclerView.addItemDecoration(new SimpleDividerDecoration(getResources()));
        merchantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //updateUI();

        bundle = getArguments();


        return view;
    }

    private void updateUI(){
        mAdapter = new MerchantAdapter(merchants);
        merchantRecyclerView.setAdapter(mAdapter);
    }

    private class MerchantHolder extends RecyclerView.ViewHolder{
        private Merchants merchants;
        public ImageView logoView;
        public TextView merchantNameView;

        public MerchantHolder(View itemView){
            super(itemView);
            mainActivity = new MainActivity();
            logoView = (ImageView) itemView.findViewById(R.id.imageView);
            merchantNameView = (TextView) itemView.findViewById(R.id.textview_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // merchantCallback.onMerchantSelected(merchants.getMerchantName());

                    Toast.makeText(getActivity(), merchants.getMerchantId() + " " + merchants.getApiUrl(), Toast.LENGTH_SHORT).show();
                    if( merchants.getPaymentType().trim().equals("full")){
                        //sending data from fragment to external activity
                        merchantIntent = new Intent(getContext(), PaymentTransactionActivity.class);
                        merchantIntent.putExtra("Merchant Id", merchants.getMerchantId());
                        merchantIntent.putExtra("Merchant Name", merchants.getMerchantName());
                        merchantIntent.putExtra("Merchant UserId", merchants.getMerchantUserID());
                        merchantIntent.putExtra("User Id", mainActivity.getUserID());
                        merchantIntent.putExtra("Merchant Api Url", merchants.getApiUrl());
                        startActivity(merchantIntent);
                    }else {
                       Toast.makeText(getContext(),"coming soon...", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        public void bindData(Merchants s){
            merchants = s;
            //logoView.setImageResource(s.getLogoId());
            Picasso.with(getContext())
                    .load(IMAGE_URL + s.getImageName() + ".JPG")
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.ic_account_circle_white_48dp)// optional
                    .error(R.drawable.ic_account_circle_black_48dp)// optional
                    .into(logoView);
            merchantNameView.setText(s.getMerchantName());
            //mBirthDeathTextView.setText(s.getBirthYear()+"-"+s.getDeathYear());
        }
    }

    private class MerchantAdapter extends RecyclerView.Adapter<MerchantHolder>{
        private ArrayList<Merchants> merchantsArrayList;
        public MerchantAdapter(ArrayList<Merchants> merchant){
            merchantsArrayList = merchant;
        }


        @Override
        public MerchantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.merchant,parent,false);
            return new MerchantHolder(view);

        }

        @Override
        public void onBindViewHolder(MerchantHolder holder, int position) {

            Merchants m = merchantsArrayList.get(position);
            holder.bindData(m);
        }

        @Override
        public int getItemCount() {
            return merchantsArrayList.size();

        }
    }


}
