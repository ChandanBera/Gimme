package com.xentric.gimme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.xentric.gimme.sharedhelper.UserShared;
import com.xentric.gimme.volleyhandler.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NoPaymentEntryWithReason extends AppCompatActivity
implements View.OnClickListener{

    private LinearLayout lin_menu,lin_sync;
    private TextView tv_title;
    //private AutoCompleteTextView tv_reason;
    private Button btn_submit;
    private RecyclerView recyclerView;
    private List<String> listOfReason = new ArrayList<String>();
    private String customerId,reason,userID;
    private String encodedurl;
    private ReasonAdapter adapter;
    private static final String NO_COLLECTION_URL = APIHelper.NO_COLLECTION_URL;
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_payment_entry_with_reason);

        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_sync = (LinearLayout) findViewById(R.id.lin_sync);
        tv_title = (TextView) findViewById(R.id.tv_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //tv_reason = (AutoCompleteTextView) findViewById(R.id.tv_reason);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        tv_title.setText("No Payment Entry");
        customerId = getIntent().getStringExtra("customer_id");

        btn_submit.setOnClickListener(this);
        lin_menu.setOnClickListener(this);
        lin_sync.setVisibility(View.INVISIBLE);
        listOfReason.add("Shop Closed");
        listOfReason.add("Not Interested To Take Material");
        listOfReason.add("Excess Order");
        listOfReason.add("Product Quality Problem");
        listOfReason.add("Late Supply");
        listOfReason.add("Owner Not Present");
        listOfReason.add("Traffic/Road Issues");
        listOfReason.add("Heavy Rain(***)");
        listOfReason.add("Others");

        recyclerView.setLayoutManager(new LinearLayoutManager(NoPaymentEntryWithReason.this));
        adapter = new ReasonAdapter(NoPaymentEntryWithReason.this,listOfReason);
        recyclerView.setAdapter(adapter);

    }

    private void actionSubmit() {
        if (!reason.equals("Others")){
            String response = reason;
            String resp = response.replace(" ","%20");
            try {
                encodedurl = URLEncoder.encode(response,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            UserShared userShared = new UserShared(NoPaymentEntryWithReason.this);
            userID = userShared.getUserId();
            StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                    NoPaymentEntryWithReason.this,
                    1,
                    NO_COLLECTION_URL + "/" + userID +"/" + customerId + "/" + resp,
                    new VolleyRequestHandler.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.optString("key").equals("true")){
                                    //onBackPressed();

                                    Intent intent = new Intent(NoPaymentEntryWithReason.this,CollectionEntry.class);
                                    //intent.putExtra("customer_id",customerId);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
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
            streq.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                    AppConstant.DEFAULT_MAX_RETRIES,
                    AppConstant.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(NoPaymentEntryWithReason.this).add(streq);
        }else {
            Intent intent = new Intent(NoPaymentEntryWithReason.this,NoPaymentEntryWithUnknownReason.class);
            intent.putExtra("customer_id",customerId);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                actionSubmit();
                break;
            case R.id.lin_menu:
                    onBackPressed();
                break;
        }
    }

    public class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder>{
        Context mContext;
        List<String> listOfReason;

        public ReasonAdapter(Context mContext, List<String> listOfReason) {
            this.mContext = mContext;
            this.listOfReason = listOfReason;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_reason_row,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.lin_reason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reason = listOfReason.get(position);
                    actionSubmit();
                }
            });
            holder.txtViewReason.setText(listOfReason.get(position));
        }



        @Override
        public int getItemCount() {
            return listOfReason.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout lin_reason;
            TextView txtViewReason;
            public ViewHolder(View itemView) {
                super(itemView);
                lin_reason = (LinearLayout) itemView.findViewById(R.id.lin_reason);
                txtViewReason = (TextView) itemView.findViewById(R.id.txtViewReason);
            }
        }
    }
}
