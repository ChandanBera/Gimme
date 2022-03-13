package com.xentric.gimme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xentric.gimme.apihelper.APIHelper;
import com.xentric.gimme.apihelper.AppConstant;
import com.xentric.gimme.sharedhelper.UserShared;
import com.xentric.gimme.volleyhandler.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class NoPaymentEntryWithUnknownReason extends AppCompatActivity
implements View.OnClickListener{

    private EditText et_reason;
    private Button btn_submit,btn_cancel;
    private LinearLayout lin_menu,lin_sync;
    private TextView tv_title;
    private String customerId,userID;
    private static final String NO_COLLECTION_URL = APIHelper.NO_COLLECTION_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_payment_entry_with_unknown_reason);

        et_reason = (EditText) findViewById(R.id.et_reason);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_sync = (LinearLayout) findViewById(R.id.lin_sync);
        tv_title = (TextView) findViewById(R.id.tv_title);

        customerId = getIntent().getStringExtra("customer_id");

        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        lin_menu.setOnClickListener(this);
        lin_sync.setVisibility(View.INVISIBLE);
        tv_title.setText("No Payment Entry");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                    submitReason();
                break;
            case R.id.btn_cancel:
                break;

            case R.id.lin_menu:
                break;
        }
    }

    private void submitReason() {
        final String reason = et_reason.getText().toString();
        String res = reason.replace(" ","%20");
        UserShared userShared = new UserShared(NoPaymentEntryWithUnknownReason.this);
        userID = userShared.getUserId();
        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                NoPaymentEntryWithUnknownReason.this,
                1,
                NO_COLLECTION_URL + "/" + userID + "/" + customerId + "/" + res,
                new VolleyRequestHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.optString("key").equals("true")){
                                /*Intent intent = new Intent(NoPaymentEntryWithUnknownReason.this,CollectionEntry.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);*/


                                Intent intent = new Intent(NoPaymentEntryWithUnknownReason.this,CollectionEntry.class);
                                intent.putExtra("customer_id",customerId);
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
        Volley.newRequestQueue(NoPaymentEntryWithUnknownReason.this).add(streq);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
