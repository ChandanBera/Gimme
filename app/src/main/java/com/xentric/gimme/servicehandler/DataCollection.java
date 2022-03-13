package com.xentric.gimme.servicehandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xentric.gimme.apihelper.APIHelper;
import com.xentric.gimme.apihelper.AppConstant;
import com.xentric.gimme.beanmodelhelper.CustomerModel;
import com.xentric.gimme.beanmodelhelper.UserModel;
import com.xentric.gimme.dbhelper.DBHelper;
import com.xentric.gimme.volleyhandler.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by goutam on 13/6/16.
 */
public class DataCollection extends Service{
    private static final String GET_ALL_USER = APIHelper.GET_USER_URL;
    private static final String GET_ALL_CUSTOMER = APIHelper.GET_ALL_CUSTOMER_URL;
    private static final String TAG = "DATASERVICE";
    private DBHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //All User Details
        //Toast.makeText(getApplicationContext(),"Service called",Toast.LENGTH_LONG).show();
        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                getApplicationContext(),
                0,
                GET_ALL_USER,
                new VolleyRequestHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONArray userArray = new JSONArray(result);
                            if (userArray.length() > 0){
                                for (int i=0;i<userArray.length();i++){
                                    JSONObject user = userArray.getJSONObject(i);
                                    db.addUser(new UserModel(Integer.parseInt(user.optString("Id")),
                                            user.optString("UserName"),
                                            user.optString("Password"),
                                            user.optString("UserEmailAddress"),
                                            user.optString("FirstName"),
                                            user.optString("LastName"),
                                            user.optString("PhoneNumber")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(String fail) {
                        VolleyLog.e(TAG,fail);
                    }
                }
        );
        streq.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                AppConstant.DEFAULT_MAX_RETRIES,
                AppConstant.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(streq);
        //All Customer Details
        StringRequest customerRequest = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                getApplicationContext(),
                0,
                GET_ALL_CUSTOMER,
                new VolleyRequestHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONArray customerArray = new JSONArray(result);
                            if (customerArray.length() > 0){
                                for (int i=0;i<customerArray.length();i++){
                                    JSONObject customer = customerArray.getJSONObject(i);
                                    db.addCustomer(new CustomerModel(
                                            customer.optString("Id"),
                                            customer.optString("Name"),
                                            customer.optString("Code"),
                                            customer.optString("Address"),
                                            customer.optString("ContactMobile"),
                                            customer.optString("LId")
                                    ));
                                    //Log.e("Customer Adder","customer added");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String fail) {

                    }
                }
        );
        customerRequest.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                AppConstant.DEFAULT_MAX_RETRIES,
                AppConstant.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(customerRequest);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
