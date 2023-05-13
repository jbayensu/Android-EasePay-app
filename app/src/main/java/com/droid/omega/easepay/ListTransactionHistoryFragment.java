package com.droid.omega.easepay;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.droid.omega.easepay.DataClasses.History;
import com.droid.omega.easepay.DataClasses.Merchants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListTransactionHistoryFragment extends Fragment {

   // private int[] image = {R.drawable.ic_add_shopping_cart_black_48dp, R.drawable.ic_arrow_go_black, R.drawable.ic_account_balance_wallet_black_48dp,R.drawable.ic_account_circle_black_24dp};
    private ArrayList<History> histories;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter hAdapter;
    private RequestQueue requestQueue;
    private String userID;
    private String toUserID;
    private String toUserName;
    private String fromUserID;
    private String transactionId;
    private String description;
    private String amount;
    private String dateTime;
    private String successStatus;
    private MainActivity mainActivity;
    private final String IMAGE_URL = "http://192.168.202.3:82/EasePay/images/";
    private final String TRANSACTION_URL = "http://192.168.202.3:82/EasePay/TransactionList.php";

    public ListTransactionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = new MainActivity();
        userID = mainActivity.getUserID();

        final ProgressDialog sending = ProgressDialog.show(getContext(),"Loading Histories...","Please wait...",false,false);
        histories = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TRANSACTION_URL, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                sending.dismiss();
                try{
                    JSONArray transactionList = response.getJSONArray("Transaction List");

                    for(int i=0; i<transactionList.length(); i++){
                        History h = new History();
                        JSONObject transaction = transactionList.getJSONObject(i);

                        toUserID = transaction.getString("toUserId");
                        fromUserID = transaction.getString("fromUserId");

                        if(toUserID.trim().equals(userID.trim()) || fromUserID.trim().equals(userID.trim())){

                            transactionId = transaction.getString("transactionId");
                            amount = transaction.getString("amount");
                            description = transaction.getString("description");
                            dateTime = transaction.getString("transactionDate");
                            successStatus = transaction.getString("successStatus");
                            toUserName = transaction.getString("toUserName");
                            h.setTransactionId(transactionId);
                            h.setImageName(toUserID);
                            h.setFromUserId(fromUserID);
                            h.setAmountPaid(amount);
                            h.setItemDesc(description);
                            h.setDatePaid(dateTime);
                            h.setSuccessStatus(successStatus);
                            h.setToUserId(toUserID);
                            h.setToUserName(toUserName);
                            histories.add(h);
                        }
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
                Log.i("error", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_transaction_history, container, false);
        historyRecyclerView = (RecyclerView) view.findViewById(R.id.history_recycler_view);
        //historyRecyclerView.addItemDecoration(new SimpleDividerDecoration(getResources()));
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


       // updateUI();
        return view;
    }

    private void updateUI(){
        hAdapter = new HistoryAdapter(histories);
        historyRecyclerView.setAdapter(hAdapter);
    }

    private class HistoryHolder extends RecyclerView.ViewHolder{
        private History history;
        public ImageView toUserlogoView;
        public TextView merchantNameView, itemDescView, amountPaidView, datePaidView, successStatusView, transactionIdView, fromUserView;


        public HistoryHolder(View itemView){
            super(itemView);
            toUserlogoView = (ImageView) itemView.findViewById(R.id.merchantImage);
            fromUserView = (TextView) itemView.findViewById(R.id.fromUser);
            merchantNameView = (TextView) itemView.findViewById(R.id.merchant_name);
            itemDescView = (TextView) itemView.findViewById(R.id.desc_name);
            amountPaidView = (TextView) itemView.findViewById(R.id.amount_paid);
            datePaidView = (TextView) itemView.findViewById(R.id.Date_Time);
            successStatusView = (TextView) itemView.findViewById(R.id.success_status);
            transactionIdView = (TextView) itemView.findViewById(R.id.transaction_Id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),
                           // history.getMerchantName() + " clicked!", Toast.LENGTH_SHORT)
                            //.show();
                }
            });
        }
        public void bindData(History h){
            history = h;
            Picasso.with(getContext())
                    .load(IMAGE_URL + h.getImageName() + ".JPG")
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.ic_account_circle_white_48dp)// optional
                    .error(R.drawable.ic_account_circle_black_48dp)// optional
                    .into(toUserlogoView);
            if(h.getToUserName().equals("")) {
                merchantNameView.setText("To: " + h.getToUserId());
            }else{
                merchantNameView.setText("To: " + h.getToUserName());
            }
            fromUserView.setText("From: " + h.getFromUserId());
            itemDescView.setText(h.getItemDesc());
            amountPaidView.setText("Ghc\n" + h.getAmountPaid());
            datePaidView.setText(h.getDatePaid());
            if(h.getSuccessStatus().trim().equals("1")){
                successStatusView.setText("Successful");
            }else{
                successStatusView.setText("Failed");
            }
            transactionIdView.setText(h.getTransactionId());
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder>{
        private ArrayList<History> histories;

        public HistoryAdapter(ArrayList<History> histor){
            histories = histor;
        }


        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.transaction_history,parent,false);
            return new HistoryHolder(view);

        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            History h = histories.get(position);
            holder.bindData(h);
        }

        @Override
        public int getItemCount() {
            return histories.size();
        }
    }

}
