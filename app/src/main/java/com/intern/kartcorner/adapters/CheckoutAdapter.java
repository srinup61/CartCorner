package com.intern.kartcorner.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intern.kartcorner.ui.WMCartActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.OrdersCommonClass;
import com.intern.kartcorner.ui.CartItemsActivity;
import com.intern.kartcorner.ui.ViewitemsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.intern.kartcorner.app.Constants.BASE_URL;


/**
 * Created by Hari Prahlad on 05-06-2016.
 */
public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> implements
        ItemTouchHelperAdapter {
    public Activity activity;
    private LayoutInflater mInflater;
    public List<OrdersCommonClass> allCommonClasses;
    private List<OrdersCommonClass> mFilteredList;
    SharedPreferences sharedPrefs;
    private OnItemClickListener clickListener;
    private SpotsDialog progressDailog;
    private ShowToast showToast;
    private CartItemsActivity cartItemsActivity;
    private ViewitemsActivity viewitemsActivity;
    private WMCartActivity wmCartActivity;
    private PrefManager prefManager;
    private String cartType;
    private int i;
    public CheckoutAdapter(Activity activity, List<OrdersCommonClass> allCommonClasses, CartItemsActivity cartItemsActivity,int i) {
        this.allCommonClasses = allCommonClasses;
        this.activity = activity;
        this.mFilteredList = allCommonClasses;
        mInflater = LayoutInflater.from(activity);
        progressDailog = new SpotsDialog(activity);
        showToast = new ShowToast(activity);
        this.cartItemsActivity = cartItemsActivity;
        prefManager = new PrefManager(activity);
        this.i = i;
        this.cartType = "shoppingcart";
    }
    public CheckoutAdapter(Activity activity, List<OrdersCommonClass> allCommonClasses, ViewitemsActivity cartItemsActivity, int i) {
        this.allCommonClasses = allCommonClasses;
        this.activity = activity;
        this.mFilteredList = allCommonClasses;
        mInflater = LayoutInflater.from(activity);
        progressDailog = new SpotsDialog(activity);
        showToast = new ShowToast(activity);
        this.viewitemsActivity = cartItemsActivity ;
        prefManager = new PrefManager(activity);
        this.i = i;
    }
    public CheckoutAdapter(Activity activity, List<OrdersCommonClass> allCommonClasses, WMCartActivity wmCartActivity, int i,String cartType) {
        this.allCommonClasses = allCommonClasses;
        this.activity = activity;
        this.mFilteredList = allCommonClasses;
        mInflater = LayoutInflater.from(activity);
        progressDailog = new SpotsDialog(activity);
        showToast = new ShowToast(activity);
        this.wmCartActivity = wmCartActivity ;
        prefManager = new PrefManager(activity);
        this.i = i;
        if(cartType.equals("week")){
            this.cartType = "getweekcartitems";
        }
        else {
            this.cartType = "getmonthcartitems";
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemscheckout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public int getItemCount() {
        return mFilteredList == null ? 0 : mFilteredList.size();
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final OrdersCommonClass ordersCommonClass = mFilteredList.get(position);
        holder.pName.setText(ordersCommonClass.getProductname());
        String disprice = ordersCommonClass.getProductdisprice();
        if(disprice.equals("0")){
            holder.pPrice.setText("RS."+ordersCommonClass.getProductprice());
        }else {
            holder.pPrice.setText("RS."+disprice);
        }

        String prquantity = ordersCommonClass.getProductquno()+"*"+ordersCommonClass.getProductweight();
        holder.pQuantity.setText(prquantity);
        holder.checkoutquno.setText(ordersCommonClass.getProductquno());
        Glide.with(activity)
                .load(ordersCommonClass.getProductimage())
                .into(holder.pimage);
        if(i==2){
            holder.linearLayout.setVisibility(View.GONE);
            holder.deleteitem.setVisibility(View.GONE);
            return;
        }
        holder.checkoutplus.findViewById(R.id.checkoutplus).setOnClickListener(v -> {
            OrdersCommonClass tempObj = mFilteredList.get(position);
            if (allCommonClasses.contains(tempObj)) {

                //get position of current item in shopping list
                int indexOfTempInShopingList = allCommonClasses.indexOf(tempObj);
                // update quanity in shopping list
                allCommonClasses
                        .get(indexOfTempInShopingList)
                        .setProductquno(
                                String.valueOf(Integer
                                        .parseInt(tempObj
                                                .getProductquno())+1));

                // update current item quanitity
                holder.checkoutquno.setText(tempObj.getProductquno());
                updatecart(prefManager.getUserId(),ordersCommonClass.getProductid(),holder.checkoutquno.getText().toString(),"plus");

            } else {

                tempObj.setProductquno(String.valueOf(1));

                holder.checkoutquno.setText(tempObj.getProductquno());

                allCommonClasses.add(tempObj);

            }
        });
        holder.checkoutminus.findViewById(R.id.checkoutminus).setOnClickListener(v -> {
            OrdersCommonClass tempObj = (mFilteredList).get(position);

            if (allCommonClasses
                    .contains(tempObj)) {

                int indexOfTempInShopingList = allCommonClasses.indexOf(tempObj);
                if (Integer.parseInt(tempObj.getProductquno()) != 0) {

                    allCommonClasses
                            .get(indexOfTempInShopingList)
                            .setProductquno(
                                    String.valueOf(Integer.parseInt(tempObj
                                            .getProductquno()) - 1));

                    holder.checkoutquno.setText(allCommonClasses
                            .get(indexOfTempInShopingList).getProductquno());
                    updatecart(prefManager.getUserId(),ordersCommonClass.getProductid(),holder.checkoutquno.getText().toString(),"minus");

                    if (Integer.parseInt(allCommonClasses
                            .get(indexOfTempInShopingList).getProductquno()) == 0) {

                        notifyDataSetChanged();
                    }
                }
            } else {

            }

        });

        holder.deleteitem.setOnClickListener(v -> {
            deleteitem(prefManager.getUserId(),ordersCommonClass.getProductid());

        });
    }

    private void deleteitem(String userId, String productid) {
        // do something when the button is clicked
        // do something when the button is clicked
        AlertDialog alertbox = new AlertDialog.Builder(activity,R.style.MyAlertDialogStyle)
                .setTitle("Confirmation For Delete ?")
                .setMessage("Do you want to Delete this Item from checkout ?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    progressDailog.show();
                    Ion.with(activity)
                            .load("POST",BASE_URL+"deletecartitem")
                            .setMultipartParameter("userid",userId)
                            .setMultipartParameter("productid",productid)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if(e!=null){
                                        progressDailog.dismiss();
                                        showToast.showWarningToast("Something went wrong");
                                    }else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            String error = jsonObject.getString("error");
                                            String msg  = jsonObject.getString("message");
                                            if(error.equals("false")){
                                                showToast.showSuccessToast(msg);
                                                cartItemsActivity.getcartItems(userId);
                                                if(i==3){
                                                    wmCartActivity.getcartItems(userId,cartType);
                                                }
                                                if(i==1){
                                                    cartItemsActivity.getcartItems(userId);
                                                }
                                            }else {
                                                showToast.showWarningToast(msg);
                                            }
                                            progressDailog.dismiss();
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            });
                })
                .setNegativeButton("No", (dialog, arg1) -> dialog.cancel())
                .show();
    }

    public void updateResults(List<OrdersCommonClass> results) {
        this.allCommonClasses = results;
        //Triggers the list update
        notifyDataSetChanged();
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

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        AppCompatTextView pName, pPrice, pQuantity;
        TextView checkoutminus,checkoutquno,checkoutplus;
        ImageView deleteitem,pimage;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            pName = itemView.findViewById(R.id.checkoutname);
            pPrice = itemView.findViewById(R.id.checkitemcost);
            pQuantity = itemView.findViewById(R.id.checkoutprquantity);
            checkoutminus = itemView.findViewById(R.id.checkoutminus);
            checkoutquno = itemView.findViewById(R.id.checkoutquantity);
            checkoutplus = itemView.findViewById(R.id.checkoutplus);
            pimage = itemView.findViewById(R.id.checkoutimage);
            deleteitem = itemView.findViewById(R.id.checkoutitem_delete);
            linearLayout = itemView.findViewById(R.id.calculation);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            try{
                clickListener.onItemClick(view, getPosition());
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }
    public void updatecart(String userid,String productid,String quantity,String type){
        progressDailog.show();
        String carttype = null;
        if(cartType.equals("shoppingcart")){
            carttype = "cartitems";
        } else if(cartType.equals("getweekcartitems")){
            carttype = "weeklycart";
        }else if(cartType.equals("getmonthcartitems")){
            carttype = "monthlycart";
        }
        Ion.with(activity)
                .load("POST",BASE_URL+"updateacartitem")
                .setMultipartParameter("userid",userid)
                .setMultipartParameter("productid",productid)
                .setMultipartParameter("type",type)
                .setMultipartParameter("quantity",quantity)
                .setMultipartParameter("cartType",carttype)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            progressDailog.dismiss();
                            showToast.showErrorToast("Something went wrong");
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String error = jsonObject.getString("error");
                                String meg = jsonObject.getString("message");
                                if(error.equals("false")){
                                    showToast.showSuccessToast(meg);
                                }else {
                                    showToast.showInfoToast(meg);
                                }
                                if(i==3){
                                    wmCartActivity.getcartItems(wmCartActivity.prefManager.getUserId(),cartType);
                                    progressDailog.dismiss();
                                }
                                if(i==1){
                                    cartItemsActivity.getcartItems(cartItemsActivity.prefManager.getUserId());
                                    progressDailog.dismiss();
                                }
                            } catch (JSONException e1) {
                                progressDailog.dismiss();
                                showToast.showErrorToast("Server is not available");
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

}
