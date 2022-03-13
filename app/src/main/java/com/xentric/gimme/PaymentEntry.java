package com.xentric.gimme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.xentric.gimme.apihelper.APIHelper;
import com.xentric.gimme.beanmodelhelper.CustomerModel;
import com.xentric.gimme.dbhelper.DBHelper;
import com.xentric.gimme.debughandler.LogCollection;
import com.xentric.gimme.sharedhelper.UserShared;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PaymentEntry extends AppCompatActivity
implements View.OnClickListener{

    private LinearLayout lin_menu,lin_sync;
    private TextView tv_title;
    private TextView customerName,customerCode,customerAddress;
    private Button btn_date,btn_mode_payment;
    private String custName;
    private CustomerModel customer;
    private DBHelper db;
    private LogCollection logCollection;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateFormatter2;
    private DatePickerDialog fromDatePicker;
    private JSONObject jsonObject;
    private String amount,remarks;
    private String currenDate;
    String customerId,userID;
    private static final String SAVE_COLLECTION_URL = APIHelper.SAVE_COLLECTION_URL;
    private static final String NO_COLLECTION_URL = APIHelper.NO_COLLECTION_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_entry);

        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_sync = (LinearLayout) findViewById(R.id.lin_sync);
        tv_title = (TextView) findViewById(R.id.tv_title);
        customerName = (TextView) findViewById(R.id.customerName);
        customerCode = (TextView) findViewById(R.id.customerCode);
        customerAddress = (TextView) findViewById(R.id.customerAddress);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_mode_payment = (Button) findViewById(R.id.btn_mode_payment);

        db = new DBHelper(PaymentEntry.this);
        logCollection = new LogCollection(PaymentEntry.this);
        setFromDate();
        lin_sync.setVisibility(View.INVISIBLE);
        tv_title.setText("Payment Entry");
        lin_menu.setOnClickListener(this);
        btn_mode_payment.setOnClickListener(this);
        //btn_date.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatter2 = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        Calendar c = Calendar.getInstance();
        currenDate = dateFormatter.format(c.getTime());

        btn_date.setText(currenDate);

        custName = getIntent().getStringExtra("customerName");
        customer = db.getCustomerDetails(custName);
        if (customer != null){
            customerName.setText(customer.getName());
            customerCode.setText(customer.getRefCode());
            customerAddress.setText(customer.getAddress());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_mode_payment:
                openPaymentDialog();
                break;
            case R.id.btn_date:
                fromDatePicker.show();
                break;
            case R.id.lin_menu:
                    onBackPressed();
                break;
        }
    }

    private void openPaymentDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PaymentEntry.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                PaymentEntry.this,
                android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("CASH");
        arrayAdapter.add("CHEQUE");
        arrayAdapter.add("NEFT");
        arrayAdapter.add("RTGS");
        arrayAdapter.add("CREDIT/DEBIT CARD");

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_mode_payment.setText(arrayAdapter.getItem(which));
                        if (which == 0){
                            final Dialog cashdialog = new Dialog(PaymentEntry.this);
                            cashdialog.setContentView(R.layout.cash_dialog);
                            final EditText et_amount = (EditText) cashdialog.findViewById(R.id.et_amount);
                            final EditText et_remarks = (EditText) cashdialog.findViewById(R.id.et_remarks);
                            Button btn_save = (Button) cashdialog.findViewById(R.id.btn_save);
                            Button btn_cancel = (Button) cashdialog.findViewById(R.id.btn_cancel);

                            btn_save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    amount = et_amount.getText().toString();
                                    remarks = et_remarks.getText().toString();
                                    makeRequestObjectForCash();
                                    Log.e("Request Object",jsonObject.toString());
                                    new SaveCollectionDataWithCash().execute();
                                    cashdialog.dismiss();
                                }
                            });

                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cashdialog.dismiss();
                                }
                            });
                            cashdialog.show();
                        }
                    }
                });
        builderSingle.show();
    }

    private void setFromDate() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePicker = new DatePickerDialog(this, R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {


            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                btn_date.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    public JSONObject makeRequestObjectForCash(){
        jsonObject = new JSONObject();
        Calendar c = Calendar.getInstance();
        customerId = db.getCustomerDetails(custName).getCustomerLid();
        UserShared userShared = new UserShared(PaymentEntry.this);
        userID = userShared.getUserId();
        Log.e("userID", userID);
        System.out.println("userID: "+userID+"\t username: "+userShared.getUserFullName());
        try {
            jsonObject.put("Amount",amount);
            jsonObject.put("ChequeRtgsNeftDetails","");
            jsonObject.put("CreatedBy",userID);
            jsonObject.put("Id",userID);
            jsonObject.put("PayMode","CASH");
            jsonObject.put("ChkRtgsNeftCardDate","");
            jsonObject.put("CustomerId",customerId);
            jsonObject.put("BankId",0);
            jsonObject.put("ChkRtgsNeftCardNo","");
            jsonObject.put("CardName","");
            jsonObject.put("Date",dateFormatter2.format(c.getTime()));
            jsonObject.put("Remarks",remarks);
            jsonObject.put("CardExpiryDate","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public class SaveCollectionDataWithCash extends AsyncTask<String,Void,String>{
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PaymentEntry.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonResponse;
            try {
                URL url = new URL(SAVE_COLLECTION_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonObject.toString());
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.i("Order Response",JsonResponse);
                return JsonResponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Item Entry", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            logCollection.setLogERed("Response>>>>"+s);
            pDialog.dismiss();
            try {
                JSONObject response = new JSONObject(s);
                if (response.optString("key").equals("true")){
                    //onBackPressed();
                    Intent intent = new Intent(PaymentEntry.this,ESign.class);
                    intent.putExtra("customer_id",db.getCustomerDetails(custName).getCustomerId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
