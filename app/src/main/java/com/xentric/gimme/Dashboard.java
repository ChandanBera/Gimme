package com.xentric.gimme;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.xentric.gimme.expandablelistdata.CustomExpandableListAdapter;
import com.xentric.gimme.sharedhelper.UserShared;
import com.xentric.gimme.tracking.SensorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dashboard extends AppCompatActivity
implements View.OnClickListener{

    private RelativeLayout rel_notification,rel_urgent,rel_visited_client,rel_collection;
    private LinearLayout lin_menu;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isOpen = false;
    private CustomExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private UserShared sp;

    Intent mServiceIntent;
    private SensorService mSensorService;

    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //starting tracker service
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(Dashboard.this, mSensorService.getClass());
        //if (!isMyServiceRunning(mSensorService.getClass())) {
            //startService(mServiceIntent);
            startService(new Intent(this, SensorService.class));
        //}

        rel_notification = (RelativeLayout) findViewById(R.id.rel_notification);
        rel_urgent = (RelativeLayout) findViewById(R.id.rel_urgent);
        rel_visited_client = (RelativeLayout) findViewById(R.id.rel_visited_client);
        rel_collection = (RelativeLayout) findViewById(R.id.rel_collection);
        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.list_slidermenu);
        sp = new UserShared(Dashboard.this);

        rel_notification.setOnClickListener(this);
        rel_urgent.setOnClickListener(this);
        rel_visited_client.setOnClickListener(this);
        rel_collection.setOnClickListener(this);
        lin_menu.setOnClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                null,
                0,
                0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isOpen = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isOpen = false;
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //expandableListDetail = ExpandableListData.getData();
        //expandableListTitle = new ArrayList<String>();
        getData();
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        mDrawerList.setAdapter(expandableListAdapter);
        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 1 && childPosition == 0){
                    //Toast.makeText(Dashboard.this, ""+00, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Dashboard.this,VisitedClient.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }else if (groupPosition == 7 && childPosition == 0){
                    sp.setLogout();
                    Intent intent1 = new Intent(Dashboard.this,Login.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                }
                return false;
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    public  void getData(){
        expandableListTitle = new ArrayList<String>();
        expandableListDetail = new HashMap<String, List<String>>();
        expandableListTitle.add("Todays Collection Option");
        expandableListTitle.add("Visited Clients Option");
        expandableListTitle.add("Urgent Payment Collection Required Option");
        expandableListTitle.add("Due List Of Clients Option");
        expandableListTitle.add("Monthly Billing Of Clients Option");
        expandableListTitle.add("Collection Details Option");
        expandableListTitle.add("Handed Over The Collections Option");
        expandableListTitle.add("Logout Option");

        List<String> todays_collection = new ArrayList<String>();


        List<String> visited_client_option = new ArrayList<String>();
        visited_client_option.add("Visited Clients");

        List<String> urgent_payment = new ArrayList<String>();

        List<String> due_list = new ArrayList<String>();

        List<String> monthly_bill = new ArrayList<String>();

        List<String> collection_details = new ArrayList<String>();

        List<String> handover_collection = new ArrayList<String>();

        List<String> logout = new ArrayList<String>();
        logout.add("Logout");

        expandableListDetail.put(expandableListTitle.get(0),todays_collection);
        expandableListDetail.put(expandableListTitle.get(1),visited_client_option);
        expandableListDetail.put(expandableListTitle.get(2),urgent_payment);
        expandableListDetail.put(expandableListTitle.get(3),due_list);
        expandableListDetail.put(expandableListTitle.get(4),monthly_bill);
        expandableListDetail.put(expandableListTitle.get(5),collection_details);
        expandableListDetail.put(expandableListTitle.get(6),handover_collection);
        expandableListDetail.put(expandableListTitle.get(7),logout);

        //return expandableListDetails;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_menu:
                    if (isOpen){
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        isOpen = false;
                    }else {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                        isOpen = true;
                    }
                break;
            case R.id.rel_collection:
                Intent intent = new Intent(Dashboard.this,CollectionEntry.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                break;
            case R.id.rel_notification:
                break;
            case R.id.rel_urgent:
                break;
            case R.id.rel_visited_client:
                    Intent intent1 = new Intent(Dashboard.this,VisitedClient.class);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
