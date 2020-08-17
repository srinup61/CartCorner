package com.intern.kartcorner.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.CheckoutAdapter;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.OrdersCommonClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intern.kartcorner.app.Constants.BASE_URL;


public class CartItemsActivity extends AppCompatActivity{
    @BindView(R.id.checkoutitems)
    RecyclerView checkoutrecyclerview;
    @BindView(R.id.totalAmount)
    TextView finalprice;
    @BindView(R.id.cartitemshavelayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.empty_cart)
    LinearLayout linearLayout;
    public CheckoutAdapter listCheckoutAdapter;
    public String userid;
    public ArrayList<OrdersCommonClass> commonClasses = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int itemsTotalCost=0,savingamount;
    public PrefManager prefManager;
    private ProgressDailog progressDailog;
    private ShowToast showToast;
    private String cartitemslentgh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartitems);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CartCorner");
        ButterKnife.bind(this);
        prefManager = new PrefManager(this);
        progressDailog = new ProgressDailog(this);
        showToast = new ShowToast(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        checkoutrecyclerview.setLayoutManager(llm);
        checkoutrecyclerview.setHasFixedSize(true);
        getcartItems(prefManager.getUserId());
    }

    public void getcartItems(String userId) {
        progressDailog.showDailog();
        Ion.with(this)
                .load("POST",BASE_URL+"getcartitems")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismissDailog();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            commonClasses.clear();
                            Object json = new JSONTokener(result).nextValue();
                            if (json instanceof JSONObject){

                            }
                            else if (json instanceof JSONArray){
                                JSONArray jsonArray = new JSONArray(result);
                                if(jsonArray.length()==0){
                                    progressDailog.dismissDailog();
                                    relativeLayout.setVisibility(View.GONE);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    progressDailog.dismissDailog();
                                    return;
                                }
                            }
                            savingamount = 0;
                            itemsTotalCost = 0;
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("cartitems");
                            cartitemslentgh = String.valueOf(jsonArray.length());
                            for(int i=0;i<jsonArray.length();i++){
                                OrdersCommonClass ordersCommonClass = new OrdersCommonClass();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                ordersCommonClass.setProductname(jsonObject1.getString("productname"));
                                ordersCommonClass.setProductid(jsonObject1.getString("productid"));
                                String productprice = jsonObject1.getString("productprice");
                                String productdisprice = jsonObject1.getString("productdiscountprice");
                                int pprice = Integer.parseInt(productprice);
                                int pquno = Integer.parseInt(jsonObject1.getString("productquno"));
                                int pdisprice = Integer.parseInt(productdisprice);
                                int finalprice = 0;
                                finalprice = pprice*pquno;
                                int finadisprice = 0;
                                finadisprice = pdisprice*pquno;

                                savingamount = savingamount+( finalprice-finadisprice);
                                int prquantity = Integer.parseInt(jsonObject1.getString("productquantity"));
                                int finalquantity = prquantity;
                                ordersCommonClass.setProductprice(String.valueOf(finalprice));
                                ordersCommonClass.setProductdisprice(String.valueOf(finadisprice));
                                ordersCommonClass.setProductquantity(String.valueOf(finalquantity));
                                ordersCommonClass.setProductweight(jsonObject1.getString("productweight"));
                                ordersCommonClass.setProductquno(jsonObject1.getString("productquno"));
                                ordersCommonClass.setProductimage(jsonObject1.getString("productimage"));
                                ordersCommonClass.setPriceid(jsonObject1.getString("priceid"));
                                commonClasses.add(ordersCommonClass);
                                itemsTotalCost = itemsTotalCost+finadisprice;
                            }
                            listCheckoutAdapter = new CheckoutAdapter(CartItemsActivity.this,commonClasses, CartItemsActivity.this,1);
                            checkoutrecyclerview.setAdapter(listCheckoutAdapter);
                            listCheckoutAdapter.notifyDataSetChanged();
                            finalprice.setText(String.valueOf(itemsTotalCost));
                            progressDailog.dismissDailog();
                        } catch (JSONException e1) {
                            progressDailog.dismissDailog();
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
//                Intent intent = new Intent(CartItemsActivity.this,MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @OnClick(R.id.checkoutplaceorder)
    void checkout(){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String products = gson.toJson(commonClasses);
        String totalitems = cartitemslentgh;
        String totalamount = String.valueOf(itemsTotalCost);
        Intent intent = new Intent(CartItemsActivity.this,CheckoutActivity.class);
        intent.putExtra("totalitems",totalitems);
        intent.putExtra("totalamount",totalamount);
        intent.putExtra("savingamount",String.valueOf(savingamount));
        intent.putExtra("products",products);
        intent.putExtra("carttype","shoppingcart");
        startActivity(intent);
    }
    @OnClick(R.id.startshopping_image)
    void startshopping(){
        this.finish();
//        Intent intent = new Intent(CartItemsActivity.this,MainActivity.class);
//        startActivity(intent);
//        finish();
    }
    @OnClick(R.id.startshopping)
    void startshop(){
        startshopping();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        Intent intent = new Intent(CartItemsActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
