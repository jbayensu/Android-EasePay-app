package com.droid.omega.easepay;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private String userID;
    private static String userEmail;
    private static String fullName;
    private Intent intent;
   // private Bundle bundle;
    private Intent i;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int pageDefault = 0;
    private int page;
    private int[] tabIcons = {
            R.drawable.ic_account_circle_white_48dp,
            R.drawable.ic_credit_card_white_48dp,
            R.drawable.ic_call_white_48dp
    };

    public String getUserEmail(){
        return userEmail;
    }
    public void setFullName(String fullname){
        fullName = fullname;
    }

    public String getUserID() {
        return userID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        page = getIntent().getIntExtra("page", pageDefault);
        viewPager.setCurrentItem(page);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        intent = getIntent();
        userEmail= intent.getStringExtra("email");
        userID =intent.getStringExtra("user id");
        fullName=intent.getStringExtra("full name");


        //simple way to send data from activity to child fragment
        AccountFragment aFrag = new AccountFragment();
        aFrag.setUserID(userID);
        OthersFragment oFrag = new OthersFragment();
        //oFrag.setUserID(userID);
        //oFrag.setUserEmail(userEmail);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        i = new Intent(getApplicationContext(), MainActivity.class);
        //Sending Data to another activity
        i.putExtra("user id", userID);
        i.putExtra("full name", fullName);
        i.putExtra("email", userEmail);
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);

    }



    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AccountFragment(), "Account");
        adapter.addFrag(new PaymentOptionFragment(), "Cards");
        adapter.addFrag(new OthersFragment(), "Contact Us");
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
