package com.intern.kartcorner.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intern.kartcorner.model.Prices;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.BadgeDrawable;
import com.intern.kartcorner.helper.CenterRepository;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.ProductCommonClass;
import com.intern.kartcorner.ui.AllItemsActivity;
import com.intern.kartcorner.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.intern.kartcorner.app.Constants.BASE_URL;


/**
 * Created by Hari Prahlad on 05-06-2016.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> implements ItemTouchHelperAdapter, Filterable {
    public Context activity;
    private List<ProductCommonClass> productList;
    public List<ProductCommonClass> mFilteredList;
    public List<ProductCommonClass> allCommonClasses;
    private List<Prices> pricesList;
    public OnItemClickListener clickListener;
    private PrefManager prefManager;
    private ShowToast showToast;
    private AllItemsActivity itemsActivity;
    private static String k;
    private SpotsDialog spotsDialog;
    public GridAdapter() {

    }

    public GridAdapter(Context activity, List<ProductCommonClass> allCommonClasses, AllItemsActivity itemsActivity) {
        this.activity = activity;
        this.allCommonClasses = allCommonClasses;
        this.productList = allCommonClasses;
        this.mFilteredList = allCommonClasses;
        pricesList = new ArrayList<>();
        prefManager = new PrefManager(activity);
        showToast = new ShowToast(activity);
        this.itemsActivity = itemsActivity;
        this.spotsDialog = new SpotsDialog(activity);
    }

    public void updateResults(List<ProductCommonClass> results) {
        this.allCommonClasses = results;
        //Triggers the list update
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_single, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductCommonClass commonClass = mFilteredList.get(position);
        holder.pName.setText(commonClass.getProductname());
        pricesList = commonClass.getPrices();
        try{
            if(pricesList.size() == 1){
                holder.quLayouut.setVisibility(View.VISIBLE);
                holder.pricesSpinner.setVisibility(View.GONE);
                commonClass.setProductprice(commonClass.getPrices().get(0).getProductPrice());
                commonClass.setProductdisprice(commonClass.getPrices().get(0).getProductDPrice());
                commonClass.setProductweight(commonClass.getPrices().get(0).getProductWeight());
                commonClass.setPriceId(commonClass.getPrices().get(0).getPriceId());
            }else {
                holder.quLayouut.setVisibility(View.GONE);
                holder.pricesSpinner.setVisibility(View.VISIBLE);
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(activity,R.layout.grid_single,commonClass.getPrices());
                holder.pricesSpinner.setAdapter(spinnerAdapter);
                holder.pricesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        commonClass.setProductprice(commonClass.getPrices().get(i).getProductPrice());
                        commonClass.setProductdisprice(commonClass.getPrices().get(i).getProductDPrice());
                        commonClass.setProductweight(commonClass.getPrices().get(i).getProductWeight());
                        commonClass.setPriceId(commonClass.getPrices().get(i).getPriceId());
                        String disamount = commonClass.getProductdisprice();
                        if (disamount.equals("0")) {
                            holder.pdprice.setVisibility(View.GONE);
                            holder.pPrice.setPaintFlags(holder.pPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        } else {
                            holder.pdprice.setVisibility(View.VISIBLE);
                            holder.pPrice.setPaintFlags(holder.pPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        holder.pMeasure.setText(commonClass.getProductweight());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            commonClass.setProductquantity("1");
            holder.pPrice.setText(commonClass.getProductprice());
            holder.pdprice.setText(commonClass.getProductdisprice());
            holder.quantity.setText(commonClass.getProductquantity());
        }catch (NullPointerException e){

        }

        try{
            String disamount = commonClass.getProductdisprice();
            if (disamount.equals("0")) {
                holder.pdprice.setVisibility(View.GONE);
                holder.pPrice.setPaintFlags(holder.pPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                holder.pdprice.setVisibility(View.VISIBLE);
                holder.pPrice.setPaintFlags(holder.pPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            holder.pMeasure.setText(commonClass.getProductweight());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        try {
            holder.quantity.setText(CenterRepository.getCenterRepository()
                    .getListOfProductsInShoppingList().get(position).getProductquantity());
        } catch (IndexOutOfBoundsException e) {

        }
        if (commonClass.getProductimage().equals("")) {

        } else {
            Glide.with(activity)
                    .load(commonClass.getProductimage())
                    .into(holder.pimage);
        }
        holder.plus.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCommonClass tempObj = mFilteredList.get(position);
                if (allCommonClasses.contains(tempObj)) {

                    //get position of current item in shopping list
                    int indexOfTempInShopingList = allCommonClasses
                            .indexOf(tempObj);

                    // update quanity in shopping list
                    allCommonClasses
                            .get(indexOfTempInShopingList)
                            .setProductquantity(
                                    String.valueOf(Integer
                                            .parseInt(tempObj
                                                    .getProductquantity()) + 1));

                    // update current item quanitity
                    holder.quantity.setText(tempObj.getProductquantity());

                } else {

                    tempObj.setProductquantity(String.valueOf(1));

                    holder.quantity.setText(tempObj.getProductquantity());

                    allCommonClasses.add(tempObj);

                }
            }
        });
        holder.minus.findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCommonClass tempObj = (mFilteredList).get(position);

                if (allCommonClasses
                        .contains(tempObj)) {

                    int indexOfTempInShopingList = allCommonClasses
                            .indexOf(tempObj);

                    if (Integer.parseInt(tempObj.getProductquantity()) != 0) {

                        allCommonClasses
                                .get(indexOfTempInShopingList)
                                .setProductquantity(
                                        String.valueOf(Integer.parseInt(tempObj
                                                .getProductquantity()) - 1));

                        holder.quantity.setText(allCommonClasses
                                .get(indexOfTempInShopingList).getProductquantity());

                        if (Integer.parseInt(allCommonClasses
                                .get(indexOfTempInShopingList).getProductquantity()) == 0) {

                            notifyDataSetChanged();
                        }
                    }
                } else {

                }

            }
        });

        holder.add.findViewById(R.id.additem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userid = prefManager.getUserId();
                String productname = commonClass.getProductname();
                String productid = commonClass.getProductid();
                String discprice = commonClass.getProductdisprice();
                String priceId = commonClass.getPriceId();
                String productprice;
                int totaldis;
                if (discprice.equals("0")) {
                    productprice = commonClass.getProductprice();
                    totaldis = 0;
                } else {
                    productprice = commonClass.getProductdisprice();
                    totaldis = Integer.parseInt(commonClass.getProductprice()) - Integer.parseInt(commonClass.getProductdisprice());

                }
                String productdisprice = String.valueOf(totaldis);
                String productquantity = holder.quantity.getText().toString();
                String productquno = commonClass.getProductquantity();
                String productweight = commonClass.getProductweight();
                String productimage = commonClass.getProductimage();
                String qu = holder.quantity.getText().toString();
                if (qu.equals("0")) {
                    showToast.showInfoToast("Please select product quantity");
                    return;
                }
                if (!prefManager.isLoggedIn()) {
                    logindialog("You have to login first before adding items into Cart ?");
                    return;
                }
                addproducttocart(userid,priceId, productname, productid, productprice, productdisprice, productquantity, productquno, productweight, productimage);
            }

            private void addproducttocart(String userid,String priceid, String productname, String productid, String productprice, String productdisprice, String productquantity, String productquno, String productweight, String productimage) {
                spotsDialog.show();
                Ion.with(activity)
                        .load("POST", BASE_URL + "addproducttocart")
                        .setMultipartParameter("userid", userid)
                        .setMultipartParameter("priceid", priceid)
                        .setMultipartParameter("productid", productid)
                        .setMultipartParameter("productname", productname)
                        .setMultipartParameter("productprice", productprice)
                        .setMultipartParameter("productdiscountprice", productdisprice)
                        .setMultipartParameter("productquantity", productquno)
                        .setMultipartParameter("productquno", productquantity)
                        .setMultipartParameter("productweight", productweight)
                        .setMultipartParameter("productimage", productimage)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e != null) {
                                    spotsDialog.dismiss();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        String error = jsonObject.getString("error");
                                        String msg = jsonObject.getString("message");
                                        if (error.equals("false")) {
                                            showToast.showSuccessToast(msg);
                                            spotsDialog.dismiss();
                                            badgecount();
                                        } else {
                                            showToast.showErrorToast(msg);
                                            spotsDialog.dismiss();
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                        spotsDialog.dismiss();
                                    }
                                }
                            }
                        });
            }
        });


    }
    public List<ProductCommonClass> getItems() {
        return productList;
    }
    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mFilteredList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mFilteredList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = productList;
                } else {

                    ArrayList<ProductCommonClass> filteredList = new ArrayList<>();

                    for (ProductCommonClass androidVersion : productList) {

                        if (androidVersion.getProductname().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ProductCommonClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onItemDismiss(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView pId, ppid, puid, pName, pPrice, pdprice, pQuantity, pMeasure, pdesc, pimageUrl;
        private TextView quantity;
        TextView plus;
        TextView minus;
        Button add;
        ImageView pimage;
        public AppCompatSpinner pricesSpinner;
        public LinearLayout quLayouut;

        public ViewHolder(View itemView) {
            super(itemView);
            pName = (TextView) itemView.findViewById(R.id.productname);
            pPrice = (TextView) itemView.findViewById(R.id.vendoreprice);
            pdprice = (TextView) itemView.findViewById(R.id.pdiscprice);
            pQuantity = (TextView) itemView.findViewById(R.id.productquantity);
            pMeasure = (TextView) itemView.findViewById(R.id.cartmeasure);
            pimage = (ImageView) itemView.findViewById(R.id.productimage);
            add = (Button) itemView.findViewById(R.id.additem);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            plus = (TextView) itemView.findViewById(R.id.plus);
            minus = (TextView) itemView.findViewById(R.id.minus);
            pricesSpinner = itemView.findViewById(R.id.prices);
            quLayouut = itemView.findViewById(R.id.quLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(view, getPosition());
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    private void logindialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle).create();
        // Setting Dialog Title
        alertDialog.setTitle("Confirmation For Login ?");
        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, id) -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.putExtra("getback", "items");
            ((Activity) activity).startActivityForResult(intent, 4);
            dialog.dismiss();
            return;

        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                return;
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void badgecount() {
        prefManager = new PrefManager(activity);
        Ion.with(activity)
                .load("POST", BASE_URL + "getcartitems")
                .setMultipartParameter("userid", prefManager.getUserId())
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("cartitems");
                            k = String.valueOf(jsonArray.length());
                            itemsActivity.setBadgeCount(activity, k);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
