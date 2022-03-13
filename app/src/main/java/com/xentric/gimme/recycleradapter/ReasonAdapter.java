package com.xentric.gimme.recycleradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xentric.gimme.R;

import java.util.List;

/**
 * Created by GOUTAM on 9/5/2016.
 */

public class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder>{
    Context mContext;
    List<String> listOfReason;

    public ReasonAdapter(Context mContext, List<String> listOfReason) {
        this.mContext = mContext;
        this.listOfReason = listOfReason;
    }

    @Override
    public ReasonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reason_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReasonAdapter.ViewHolder holder, int position) {
        holder.lin_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
