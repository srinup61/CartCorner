package com.intern.kartcorner.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.intern.kartcorner.R;
import com.intern.kartcorner.entities.PlaceOrderCommonClass;
import com.intern.kartcorner.ui.ViewitemsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Prabhu on 12-12-2017.
 */

public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    public Activity activity;
    private LayoutInflater mInflater;
    public List<PlaceOrderCommonClass> allCommonClasses = new ArrayList<>();
    SharedPreferences sharedPrefs;
    private OnItemClickListener clickListener;

    public PlaceOrderAdapter(Activity activity, List<PlaceOrderCommonClass> allCommonClasses) {
        this.allCommonClasses = allCommonClasses;
        this.activity = activity;

    }

    @Override
    public PlaceOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.single_order, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.orderId.setText(allCommonClasses.get(position).getOrderid());
        holder.deliverydate.setText(allCommonClasses.get(position).getDeliverydate());
        holder.deliverytime.setText(allCommonClasses.get(position).getDeliverytime());
        holder.totalpayable.setText(allCommonClasses.get(position).getTotalamount());
        holder.discountamount.setText(allCommonClasses.get(position).getDiscountamount());
        holder.orderstatus.setText(allCommonClasses.get(position).getOrderstatus());
        holder.orderitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ViewitemsActivity.class);
                intent.putExtra("items",allCommonClasses.get(position).getProducts());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allCommonClasses == null ? 0 : allCommonClasses.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(allCommonClasses, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(allCommonClasses, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        allCommonClasses.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView orderId, orderitems, deliverydate, deliverytime, totalpayable,discountamount,orderstatus;

        public ViewHolder(View itemView) {
            super(itemView);
            orderId = (TextView) itemView.findViewById(R.id.singleorderId);
            orderitems = (TextView) itemView.findViewById(R.id.vieworders);
            deliverydate = (TextView) itemView.findViewById(R.id.orderDeliveryDate);
            deliverytime = (TextView) itemView.findViewById(R.id.orderDeliveryTime);
            totalpayable = (TextView) itemView.findViewById(R.id.orderDeliverypayable);
            discountamount = (TextView) itemView.findViewById(R.id.orderDiscountamount);
            orderstatus= (TextView)itemView.findViewById(R.id.orderDeliverystatus);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            try{
                clickListener.onItemClick(view, getPosition());
            }catch (NullPointerException e){

            }

        }
    }
}
