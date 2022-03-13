package com.xentric.gimme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xentric.gimme.beanmodelhelper.ClientModel;
import com.xentric.gimme.sharedhelper.UserShared;
import com.xentric.gimme.volleyhandler.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VisitedClient extends AppCompatActivity
implements View.OnClickListener{

    private RecyclerView recyclerView;
    private LinearLayout lin_menu,lin_sync;
    private TextView tv_title;
    private ClientAdapter adapter;
    private ArrayList<ClientModel> listOfClient = new ArrayList<ClientModel>();
    private static final String VISITED_CLIENT_URL = APIHelper.VISIT_URL;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_client);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_sync = (LinearLayout) findViewById(R.id.lin_sync);
        tv_title = (TextView) findViewById(R.id.tv_title);

        lin_menu.setOnClickListener(this);
        lin_sync.setVisibility(View.INVISIBLE);
        tv_title.setText("Visited Clients");
        recyclerView.setLayoutManager(new LinearLayoutManager(VisitedClient.this));

        UserShared userShared = new UserShared(VisitedClient.this);
        userID = userShared.getUserId();
        System.out.println("userID: "+userID+"\t username: "+userShared.getUserFullName());
        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequestusinGet(
                VisitedClient.this,
                1,
                VISITED_CLIENT_URL + "/" + userID,
                new VolleyRequestHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONArray jsonArry = new JSONArray(result);
                            if (jsonArry.length() > 0){
                                for (int i=0;i<jsonArry.length();i++){
                                    JSONObject object = jsonArry.getJSONObject(i);
                                    listOfClient.add(new ClientModel(
                                            object.optString("Customer"),
                                            object.optString("Date"),
                                            object.optString("Amount"),
                                            object.optString("Mode"),
                                            object.optString("ReasonForNonPayment"),
                                            object.optString("SalesPerson")
                                    ));
                                    adapter = new ClientAdapter(listOfClient);
                                    recyclerView.setAdapter(adapter);
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
        streq.setRetryPolicy(new DefaultRetryPolicy(AppConstant.TIMEOUT_MS,
                AppConstant.DEFAULT_MAX_RETRIES,
                AppConstant.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VisitedClient.this).add(streq);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_menu:
                    onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    private class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder>{

        ArrayList<ClientModel> clientList;

        public ClientAdapter(ArrayList<ClientModel> clientList) {
            this.clientList = clientList;
        }

        @Override
        public ClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visited_client,parent,false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ClientAdapter.ViewHolder holder, int position) {
            ClientModel client = clientList.get(position);
            holder.tv_amount.setText(client.getAmount());
            holder.tv_customer.setText(client.getCustomerName());
            holder.tv_date.setText(client.getDate());
            holder.tv_mode.setText(client.getMode());
            holder.tv_reason.setText(client.getReason());
            holder.tv_sales.setText(client.getSalesPerson());
        }

        @Override
        public int getItemCount() {
            return clientList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout lin_item;
            TextView tv_customer;
            TextView tv_date;
            TextView tv_amount;
            TextView tv_mode;
            TextView tv_reason;
            TextView tv_sales;

            public ViewHolder(View itemView) {
                super(itemView);

                lin_item = (LinearLayout) itemView.findViewById(R.id.lin_item);
                tv_customer = (TextView) itemView.findViewById(R.id.tv_customer);
                tv_date = (TextView) itemView.findViewById(R.id.tv_date);
                tv_amount = (TextView) itemView.findViewById(R.id.tv_amount);
                tv_mode = (TextView) itemView.findViewById(R.id.tv_mode);
                tv_reason = (TextView) itemView.findViewById(R.id.tv_reason);
                tv_sales = (TextView) itemView.findViewById(R.id.tv_sales);
            }
        }
    }
}
