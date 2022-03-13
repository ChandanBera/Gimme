package com.xentric.gimme;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xentric.gimme.apihelper.APIHelper;
import com.xentric.gimme.apihelper.AppConstant;
import com.xentric.gimme.beanmodelhelper.CustomerModel;
import com.xentric.gimme.beanmodelhelper.InvoiceModel;
import com.xentric.gimme.dbhelper.DBHelper;
import com.xentric.gimme.debughandler.LogCollection;
import com.xentric.gimme.volleyhandler.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CollectionEntry extends AppCompatActivity
implements View.OnClickListener{

    private LinearLayout lin_menu,lin_sync;
    private TextView tv_title;
    private AutoCompleteTextView tv_customer_name;
    private RecyclerView recyclerview;
    private TextView tv_total;
    private TextView val_30,val_60,val_90,val_a90;
    private Button btn_payment_entry,btn_no_payment_entry,btn_from_date,btn_to_date;
    private DBHelper db;
    private LogCollection logCollection;
    private ArrayList<String> customerName;
    private String customerId = "";
    private String fromDate,toDate;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePicker,toDatePicker;
    private static final String SALES_INVOICE_BY_CUSTOMER = APIHelper.GET_SALES_INVOICE_BY_CUSTOMER;
    private static final String GET_DUES = APIHelper.GET_DUES_BY_CUSTOMER;
    ArrayList<InvoiceModel> listInvoice = new ArrayList<InvoiceModel>();
    InvoiceAdapter adapter;
    double total_value = 0;
    String custName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_entry);

        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_sync = (LinearLayout) findViewById(R.id.lin_sync);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_customer_name = (AutoCompleteTextView) findViewById(R.id.tv_customer_name);
        tv_customer_name.setEnabled(false);
        tv_total = (TextView) findViewById(R.id.tv_total);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        val_30 = (TextView) findViewById(R.id.val_30);
        val_60 = (TextView) findViewById(R.id.val_60);
        val_90 = (TextView) findViewById(R.id.val_90);
        val_a90 = (TextView) findViewById(R.id.val_a90);
        btn_payment_entry = (Button) findViewById(R.id.btn_payment_entry);
        btn_no_payment_entry = (Button) findViewById(R.id.btn_no_payment_entry);
        btn_from_date = (Button) findViewById(R.id.btn_from_date);
        btn_to_date = (Button) findViewById(R.id.btn_to_date);
        btn_to_date.setEnabled(false);
        btn_from_date.setOnClickListener(this);
        btn_to_date.setOnClickListener(this);
        db = new DBHelper(CollectionEntry.this);
        logCollection = new LogCollection(CollectionEntry.this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        setFromDate();
        setToDate();
        //Calendar c = Calendar.getInstance();
        //toDate = dateFormatter.format(c.getTime());
        //c.set(Calendar.DAY_OF_MONTH, 1);
        //fromDate = dateFormatter.format(c.getTime());

        tv_title.setText("Collection Entry");
        tv_customer_name.setText("");
        lin_sync.setVisibility(View.INVISIBLE);
        recyclerview.setLayoutManager(new LinearLayoutManager(CollectionEntry.this));
        adapter = new InvoiceAdapter(listInvoice);
        recyclerview.setAdapter(adapter);
        customerName = new ArrayList<String>();
        List<CustomerModel> listofCustomer = new ArrayList<CustomerModel>();
        listofCustomer = db.getAllCustomer();
        for (int i=0;i<listofCustomer.size();i++){
            customerName.add(listofCustomer.get(i).getName());
        }

        //Intent Action
        Intent intent = getIntent();
        String customerid = intent.getStringExtra("customer_id");
        System.out.println("********* customerid ***************: "+customerid);
        if (customerid != null || !TextUtils.isEmpty(customerid)){

            CustomerModel customer = db.getCustomerDetailsUsingId(customerid);
            String customerName = customer.getName();
            if (customerName != null){
                tv_customer_name.setText(customerName);
                customerId = db.getCustomerDetails(customerName).getCustomerId();
                Log.e("Request",SALES_INVOICE_BY_CUSTOMER + "/" + customerid + "/" + fromDate + "/" + toDate);
                //GET SALES INVOICE BY CUSTOMER
                StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                        CollectionEntry.this,
                        1,
                        SALES_INVOICE_BY_CUSTOMER + "/" + customerid + "/" + fromDate + "/" + toDate,
                        new VolleyRequestHandler.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    Log.e("Responsee",result);
                                    listInvoice.clear();
                                    JSONArray jsonArray = new JSONArray(result);
                                    if (jsonArray.length() > 0){
                                        for (int i=0;i<jsonArray.length();i++){
                                            JSONObject jobj = jsonArray.getJSONObject(i);
                                            listInvoice.add(new InvoiceModel(
                                                    jobj.optString("Id"),
                                                    jobj.optString("Date"),
                                                    jobj.optString("NO"),
                                                    jobj.optString("TotalQuantity"),
                                                    jobj.optString("Amount")));
                                            total_value  =  total_value + Double.parseDouble(jobj.optString("Amount"));
                                            logCollection.setLogERed(""+total_value);
                                            adapter.notifyDataSetChanged();
                                        }
                                        tv_total.setText(""+total_value);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String fail) {
                            }
                        }
                );
                streq.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                        AppConstant.DEFAULT_MAX_RETRIES,
                        AppConstant.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(CollectionEntry.this).add(streq);

                //Get Dues
                StringRequest stre = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                        CollectionEntry.this,
                        0,
                        GET_DUES + "/" + customerid,
                        new VolleyRequestHandler.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                logCollection.setLogERed(result);
                                try {
                                    JSONArray jsonArray = new JSONArray(result);
                                    val_30.setText(jsonArray.get(0).toString());
                                    val_60.setText(jsonArray.get(1).toString());
                                    val_90.setText(jsonArray.get(2).toString());
                                    val_a90.setText(jsonArray.get(3).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String fail) {

                            }
                        }
                );
                stre.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                        AppConstant.DEFAULT_MAX_RETRIES,
                        AppConstant.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(CollectionEntry.this).add(stre);
            }
        }

        ArrayAdapter<String> productNameAdapter = new ArrayAdapter<String> (this,android.R.layout.select_dialog_item,customerName);
        tv_customer_name.setThreshold(1);
        tv_customer_name.setAdapter(productNameAdapter);
        tv_customer_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                total_value = 0;
                custName = parent.getItemAtPosition(position).toString();
                customerId = db.getCustomerDetails(custName).getCustomerId();
                Log.e("Request",SALES_INVOICE_BY_CUSTOMER + "/" + customerId + "/" + fromDate + "/" + toDate);
                //GET SALES INVOICE BY CUSTOMER
                StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                        CollectionEntry.this,
                        1,
                        SALES_INVOICE_BY_CUSTOMER + "/" + customerId + "/" + fromDate + "/" + toDate,
                        new VolleyRequestHandler.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    Log.e("Responsee",result);
                                    JSONArray jsonArray = new JSONArray(result);
                                    if (jsonArray.length() > 0){
                                        for (int i=0;i<jsonArray.length();i++){
                                            JSONObject jobj = jsonArray.getJSONObject(i);
                                            listInvoice.add(new InvoiceModel(
                                                    jobj.optString("Id"),
                                                    jobj.optString("Date"),
                                                    jobj.optString("NO"),
                                                    jobj.optString("TotalQuantity"),
                                                    jobj.optString("Amount")));
                                            total_value  =  total_value + Double.parseDouble(jobj.optString("Amount"));
                                            logCollection.setLogERed(""+total_value);
                                            adapter.notifyDataSetChanged();
                                        }
                                        tv_total.setText(""+total_value);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String fail) {
                            }
                        }
                );
                streq.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                        AppConstant.DEFAULT_MAX_RETRIES,
                        AppConstant.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(CollectionEntry.this).add(streq);

                //Get Dues
                StringRequest stre = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                        CollectionEntry.this,
                        0,
                        GET_DUES + "/" + customerId,
                        new VolleyRequestHandler.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                logCollection.setLogERed(result);
                                try {
                                    JSONArray jsonArray = new JSONArray(result);
                                    val_30.setText(jsonArray.get(0).toString());
                                    val_60.setText(jsonArray.get(1).toString());
                                    val_90.setText(jsonArray.get(2).toString());
                                    val_a90.setText(jsonArray.get(3).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String fail) {

                            }
                        }
                );
                stre.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                        AppConstant.DEFAULT_MAX_RETRIES,
                        AppConstant.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(CollectionEntry.this).add(stre);
            }
        });

        lin_menu.setOnClickListener(this);
        btn_payment_entry.setOnClickListener(this);
        btn_no_payment_entry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_no_payment_entry:
                if (TextUtils.isEmpty(custName)){
                    logCollection.showToastLong("Please Enter a Customer");
                }else {
                    noPaymentEntry();
                }
                break;
            case R.id.btn_payment_entry:
                if (TextUtils.isEmpty(custName)){
                    logCollection.showToastLong("Please Enter a Customer");
                }else {
                    Intent intent = new Intent(CollectionEntry.this,PaymentEntry.class);
                    intent.putExtra("customerName",custName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }
                break;
            case R.id.lin_menu:
                onBackPressed();
                break;
            case R.id.btn_from_date:
                fromDatePicker.show();
                break;
            case R.id.btn_to_date:
                toDatePicker.show();
                break;
        }
    }

    private void setFromDate() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePicker = new DatePickerDialog(this, R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {


            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                btn_from_date.setText(dateFormatter.format(newDate.getTime()));
                fromDate = btn_from_date.getText().toString();
                btn_to_date.setEnabled(true);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    private void setToDate() {
        Calendar newCalendar = Calendar.getInstance();
        toDatePicker = new DatePickerDialog(this, R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {


            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                btn_to_date.setText(dateFormatter.format(newDate.getTime()));
                toDate = btn_to_date.getText().toString();
                tv_customer_name.setEnabled(true);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    //No Payment Entry
    private void noPaymentEntry() {
        Intent intent = new Intent(CollectionEntry.this,NoPaymentEntryWithReason.class);
        intent.putExtra("customer_id",customerId);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder>{

        ArrayList<InvoiceModel> listOfInvoice;

        public InvoiceAdapter(ArrayList<InvoiceModel> listOfInvoice) {
            this.listOfInvoice = listOfInvoice;
        }

        @Override
        public InvoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice,parent,false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(InvoiceAdapter.ViewHolder holder, int position) {
            holder.tv_amount.setText(listOfInvoice.get(position).getAmount());
            holder.tv_date.setText(listOfInvoice.get(position).getDate());
            holder.tv_bill.setText(listOfInvoice.get(position).getBillNo());
            holder.tv_totalquantity.setText(listOfInvoice.get(position).getTotalQuantity());
            holder.lin_item.setTag(listOfInvoice.get(position).getInvoiceId());
            holder.lin_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return listOfInvoice.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout lin_item;
            private TextView tv_date,tv_bill,tv_totalquantity,tv_amount;

            public ViewHolder(View itemView) {
                super(itemView);
                lin_item = (LinearLayout) itemView.findViewById(R.id.lin_item);
                tv_date = (TextView) itemView.findViewById(R.id.tv_date);
                tv_bill = (TextView) itemView.findViewById(R.id.tv_bill);
                tv_totalquantity = (TextView) itemView.findViewById(R.id.tv_totalquantity);
                tv_amount = (TextView) itemView.findViewById(R.id.tv_amount);
            }
        }
    }
}
