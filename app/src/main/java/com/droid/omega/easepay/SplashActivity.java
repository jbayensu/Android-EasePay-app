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
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {


    private String errorMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WifiManager wifiOn = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifiOn.isWifiEnabled()) {
            BroadcastReceiver broadcastReceiver = new WifiBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);

        }else {
            showError("Please, check wifi connection");
        }


    }
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if(SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED){
                   checkConnectedToDesiredWifi();

                }
            }
        }

        private boolean checkConnectedToDesiredWifi(){
            boolean connected = false;

            /**String desiredMacAddress = "5a:9f:fa:9a:ac:f8";

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifi = wifiManager.getConnectionInfo();
            if (wifi != null) {
                String bssid = wifi.getBSSID();
                connected = desiredMacAddress.equals(bssid);
                if (!connected) {
                    errorMsg = "Please Check to be sure you are connected to the right wifi connection";
                    showError(errorMsg);
                }else {**/
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
            connected = true;
               /** }
            } else {
                errorMsg = "Sorry, could not get wifi information";
                showError(errorMsg);
            }**/


            return connected;
        }
    }

    public void showError(String Msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Msg).setTitle("EasePay").setCancelable(false).setPositiveButton("Exit",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
