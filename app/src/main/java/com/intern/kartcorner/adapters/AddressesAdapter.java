package com.intern.kartcorner.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.entities.MyAddressDAO;
import com.intern.kartcorner.fragments.MyAddressesFragment;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.ui.CheckoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    private ArrayList<MyAddressDAO> mValues;
    private Context mContext;
    private ShowToast showToast;
    private ProgressDailog progressDailog;
    private MyAddressesFragment myAddressesFragment;
    private CheckoutActivity checkoutActivity;
    private PrefManager prefManager;
    private int i = 0;

    public AddressesAdapter(Context context, ArrayList<MyAddressDAO> values, MyAddressesFragment myAddressesFragment,int i) {
        mValues = values;
        mContext = context;
        showToast = new ShowToast(context);
        progressDailog = new ProgressDailog(context);
        this.myAddressesFragment = myAddressesFragment;
        prefManager = new PrefManager(context);
        this.i = i;
    }
    public AddressesAdapter(Context context, ArrayList<MyAddressDAO> values, CheckoutActivity checkoutActivity,int i) {
        mValues = values;
        mContext = context;
        showToast = new ShowToast(context);
        progressDailog = new ProgressDailog(context);
        this.checkoutActivity = checkoutActivity;
        prefManager = new PrefManager(context);
        this.i = i;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView address,nickname;
        private ImageView update,delete;
        private AppCompatCheckBox defaulttype;

        public ViewHolder(View v) {
            super(v);
            nickname = v.findViewById(R.id.addressnickname);
            address = v.findViewById(R.id.addressName);
            update = v.findViewById(R.id.updateAddress);
            delete = v.findViewById(R.id.deleteAddress);
            defaulttype = v.findViewById(R.id.default_type);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.deliveryaddresssingle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MyAddressDAO myAddressDAO = mValues.get(position);
        viewHolder.nickname.setText(myAddressDAO.getAddressnickname());
        viewHolder.address.setText(myAddressDAO.getAddress());
        String defaulttype = myAddressDAO.getDefaulttype();
        if(defaulttype.equals("1")){
            viewHolder.defaulttype.setChecked(true);
        }else {
            viewHolder.defaulttype.setChecked(false);
        }
        viewHolder.defaulttype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // do something when the button is clicked
                AlertDialog alertbox = new AlertDialog.Builder(mContext,R.style.MyAlertDialogStyle)
                        .setTitle("Confirmation For Set Default Address ?")
                        .setMessage("Do you want to Continue ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                progressDailog.showDailog();
                                String addressid  = myAddressDAO.getAddressid();
                                String address = myAddressDAO.getAddress();
                                String latlong = myAddressDAO.getLatlong();
                                String userid = prefManager.getUserId();
                                setasdefault(addressid,address,latlong,userid);
                            }

                            private void setasdefault(String addressid, String address, String latlong, String userid) {
                                Ion.with(mContext)
                                        .load("POST",BASE_URL+"setdefaultaddress")
                                        .setMultipartParameter("userid",userid)
                                        .setMultipartParameter("addressid",addressid)
                                        .setMultipartParameter("address",address)
                                        .setMultipartParameter("latlong","none")
                                        .asString()
                                        .setCallback((e, result) -> {
                                            if(e!=null){
                                                progressDailog.dismissDailog();
                                                showToast.showWarningToast("Something went wrong");
                                            }else {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(result);
                                                    String error = jsonObject.getString("error");
                                                    String msg  = jsonObject.getString("message");
                                                    if(error.equals("false")){
                                                        showToast.showSuccessToast(msg);
                                                        if(i==1){
                                                            myAddressesFragment.getaddresses(prefManager.getUserId());
                                                        }else {
                                                            checkoutActivity.getaddresses(prefManager.getUserId());
                                                        }

                                                    }else {
                                                        showToast.showWarningToast(msg);
                                                    }
                                                    progressDailog.dismissDailog();
                                                } catch (JSONException e1) {
                                                    progressDailog.dismissDailog();
                                                    e1.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", (dialog, arg1) -> {
                            viewHolder.defaulttype.setChecked(false);
                            dialog.cancel();
                        })
                        .show();
            }
        });
        viewHolder.update.setOnClickListener(view -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.update_address, null);
            builder.setView(dialogView);
            builder.setTitle("Update Address");
            final AlertDialog alertDialog = builder.create();
            final EditText nickname = (EditText)dialogView.findViewById(R.id.addressnicknameedital);
            nickname.setText(myAddressDAO.getAddressnickname());
            final EditText personname = (EditText)dialogView.findViewById(R.id.addresspersonnameedital);
            personname.setText(myAddressDAO.getPersonname());
            final EditText address = (EditText)dialogView.findViewById(R.id.addressedital);
            address.setText(myAddressDAO.getAddress());
            Button update = (Button)dialogView.findViewById(R.id.saveAddressal);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDailog.showDailog();
                    String addressid = myAddressDAO.getAddressid();
                    String newaddress = address.getText().toString();
                    String newnickname = nickname.getText().toString();
                    String newpersonname = personname.getText().toString();
                    if(newaddress.isEmpty()||newnickname.isEmpty()||newpersonname.isEmpty()){
                        progressDailog.dismissDailog();
                        showToast.showWarningToast("All fields are mandotory");
                        return;
                    }
                    updateaddress(addressid,newaddress,newnickname,newpersonname);
                }

                private void updateaddress(String addressid, String newaddress, String newnickname, String newpersonname) {
                    Ion.with(mContext)
                            .load("POST",BASE_URL+"updateaddress")
                            .setMultipartParameter("addressid",addressid)
                            .setMultipartParameter("address",newaddress)
                            .setMultipartParameter("personname",newpersonname)
                            .setMultipartParameter("addressnickname",newnickname)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if(e!=null){
                                            progressDailog.dismissDailog();
                                    }else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            String error = jsonObject.getString("error");
                                            String msg  = jsonObject.getString("message");
                                            if(error.equals("false")){
                                                showToast.showSuccessToast(msg);
                                                alertDialog.dismiss();
                                                if(i==1){
                                                    myAddressesFragment.getaddresses(prefManager.getUserId());
                                                }else {
                                                    checkoutActivity.getaddresses(prefManager.getUserId());
                                                }                                            }else {
                                                showToast.showWarningToast(msg);
                                            }
                                            progressDailog.dismissDailog();
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            });
                }
            });
            Button cancel = (Button)dialogView.findViewById(R.id.cancelAddressal);
            cancel.setOnClickListener(view1 -> alertDialog.dismiss());
            alertDialog.show();
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationfordelete();
            }

            private void confirmationfordelete() {
                AlertDialog alertbox = new AlertDialog.Builder(mContext,R.style.MyAlertDialogStyle)
                        .setTitle("Confirmation For Delete ?")
                        .setMessage("Do you want to Delete this address ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                progressDailog.showDailog();
                                String addressid  = myAddressDAO.getAddressid();
                                Ion.with(mContext)
                                        .load("POST",BASE_URL+"deleteaddress")
                                        .setMultipartParameter("addressid",addressid)
                                        .asString()
                                        .setCallback(new FutureCallback<String>() {
                                            @Override
                                            public void onCompleted(Exception e, String result) {
                                                if(e!=null){
                                                    progressDailog.dismissDailog();
                                                    showToast.showWarningToast("Something went wrong");
                                                }else {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(result);
                                                        String error = jsonObject.getString("error");
                                                        String msg  = jsonObject.getString("message");
                                                        if(error.equals("false")){
                                                            showToast.showSuccessToast(msg);
                                                            if(i==1){
                                                                myAddressesFragment.getaddresses(prefManager.getUserId());
                                                            }else {
                                                                checkoutActivity.getaddresses(prefManager.getUserId());
                                                            }
                                                        }else {
                                                            showToast.showWarningToast(msg);
                                                        }
                                                        progressDailog.dismissDailog();
                                                    } catch (JSONException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            // do something when the button is clicked
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.cancel();
                            }
                        })
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void onItemDismiss(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public void remove(int position) {
        if (position < 0 || position >= mValues.size()) {
            return;
        }
        mValues.remove(position);
        notifyItemRemoved(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, mValues.size());
        notifyDataSetChanged();
    }
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mValues, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mValues, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }
}
