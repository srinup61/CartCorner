package com.intern.kartcorner.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intern.kartcorner.adapters.SpinnerAdapter;
import com.intern.kartcorner.model.Prices;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.BadgeDrawable;
import com.intern.kartcorner.helper.CenterRepository;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.OrdersCommonClass;
import com.intern.kartcorner.model.ProductCommonClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class SingleItemActivity extends AppCompatActivity implements View.OnClickListener {

    public AppCompatTextView productName, productPrice, prodctDescription;
    private ImageView ProductImage;
    private Button singleac;
    private TextView singleQunatity;
    private TextView singleIncrease, singleDecrease;
    public static SharedPreferences sskey, item;
    private String Pname, Pid, PPrice, Pdisprice, prquno, Pdesc, Pimage;
    private ArrayList<Prices> prices;
    private ArrayAdapter<String> prodquAdapter;
    private static String Quantity, ppid, ProductMeasure, Productpr;
    public String FinalPrice;
    private String category;
    private String uid, qu;
    private static LayerDrawable icon;
    private List<ProductCommonClass> classes = new ArrayList<>();
    private ProductCommonClass productCommonClass;
    private OrdersCommonClass commonClass;
    private ShowToast showToast;
    private PrefManager prefManager;
    private AppCompatSpinner pricesSpinner;
    private SpotsDialog spotsDialog;
    private static String k = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CartCorner");
        showToast = new ShowToast(this);
        prefManager = new PrefManager(this);
        spotsDialog = new SpotsDialog(this);
        prices = new ArrayList<>();
        pricesSpinner = findViewById(R.id.singleprices);
        ProductImage = (ImageView) findViewById(R.id.singleprimage);
        productName = findViewById(R.id.singlepname);
        productPrice = findViewById(R.id.singlepdiscprice);
        prodctDescription = findViewById(R.id.singleproductDescription);
        singleQunatity = (TextView) findViewById(R.id.singleQuantity);
        singleDecrease = (TextView) findViewById(R.id.singleDecrese);
        singleDecrease.setOnClickListener(this);
        singleIncrease = (TextView) findViewById(R.id.singleIncrease);
        singleIncrease.setOnClickListener(this);
        singleac = (Button) findViewById(R.id.singleaddtoCart);
        singleac.setOnClickListener(this);
        classes = CenterRepository.getCenterRepository().getListInCart();
        if(prefManager.getUserId()!=null){
            badgecount();
        }
        initialize();
    }

    private void initialize() {
        Intent intent = getIntent();
        productCommonClass = (ProductCommonClass) intent.getSerializableExtra("productdata");

        uid = prefManager.getUserId();
        Pid = productCommonClass.getProductid();
        Pname = productCommonClass.getProductname();
        Pdesc = productCommonClass.getProductdesc();
        prices = productCommonClass.getPrices();
        Pimage = productCommonClass.getProductimage();

        if(prices.size() == 1){
            Pdisprice = productCommonClass.getPrices().get(0).getProductDPrice();
            PPrice = productCommonClass.getPrices().get(0).getProductPrice();
            prquno = productCommonClass.getPrices().get(0).getProductWeight();
            productCommonClass.setProductprice(productCommonClass.getPrices().get(0).getProductPrice());
            productCommonClass.setProductdisprice(productCommonClass.getPrices().get(0).getProductDPrice());
            productCommonClass.setPriceId(productCommonClass.getPrices().get(0).getPriceId());
            productCommonClass.setProductweight(productCommonClass.getPrices().get(0).getProductWeight());
            Pdisprice = productCommonClass.getPrices().get(0).getProductDPrice();
            PPrice = productCommonClass.getPrices().get(0).getProductPrice();
            singleQunatity.setText(prquno);
            if (Pdisprice.equals("0")) {
                productPrice.setText(PPrice);
            } else {
                productPrice.setText(Pdisprice);
            }
            Quantity = productCommonClass.getPrices().get(0).getProductQuantity();
            singleQunatity.setText(Quantity);
        }else {
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this,R.layout.grid_single,productCommonClass.getPrices());
            pricesSpinner.setAdapter(spinnerAdapter);
            pricesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Pdisprice = productCommonClass.getPrices().get(i).getProductDPrice();
                    PPrice = productCommonClass.getPrices().get(i).getProductPrice();
                    prquno = productCommonClass.getPrices().get(i).getProductWeight();
                    productCommonClass.setProductprice(productCommonClass.getPrices().get(i).getProductPrice());
                    productCommonClass.setProductdisprice(productCommonClass.getPrices().get(i).getProductDPrice());
                    productCommonClass.setPriceId(productCommonClass.getPrices().get(i).getPriceId());
                    productCommonClass.setProductweight(productCommonClass.getPrices().get(i).getProductWeight());
                    Pdisprice = productCommonClass.getPrices().get(i).getProductDPrice();
                    PPrice = productCommonClass.getPrices().get(i).getProductPrice();
                    singleQunatity.setText(prquno);
                    if (Pdisprice.equals("0")) {
                        productPrice.setText(PPrice);
                    } else {
                        productPrice.setText(Pdisprice);
                    }
                    Quantity = productCommonClass.getPrices().get(i).getProductQuantity();
                    singleQunatity.setText(Quantity);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        productName.setText(Pname);
        prodctDescription.setText(Html.fromHtml(Html.fromHtml(Pdesc).toString()));
        Glide.with(this)
                .load(Pimage)
                .into(ProductImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemCart = menu.findItem(R.id.cart);
        icon = (LayerDrawable) itemCart.getIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.cart:
                Intent intent = new Intent(this, CartItemsActivity.class);
                startActivity(intent);
                break;
            default:

        }
        return false;
    }

    public void setBadgeCount(Context context, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        badgecount();
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singleIncrease:
                int i = Integer.parseInt(Quantity);
                i++;
                singleQunatity.setText(String.valueOf(i));
                Quantity = String.valueOf(i);
                break;
            case R.id.singleDecrese:
                int j = Integer.parseInt(Quantity);
                if (j == 0) {

                } else {
                    j--;
                    singleQunatity.setText(String.valueOf(j));
                    Quantity = String.valueOf(j);
                }

                break;
            case R.id.singleaddtoCart:
                String userid = prefManager.getUserId();
                String productname = productCommonClass.getProductname();
                String productid = productCommonClass.getProductid();
                String discprice = productCommonClass.getProductdisprice();
                String productprice;
                int totaldis;
                if (discprice.equals("0")) {
                    productprice = productCommonClass.getProductprice();
                    totaldis = 0;
                } else {
                    productprice = productCommonClass.getProductdisprice();
                    totaldis = Integer.parseInt(productCommonClass.getProductprice()) - Integer.parseInt(productCommonClass.getProductdisprice());

                }
                String productdisprice = String.valueOf(totaldis);
                String productquantity = Quantity;
                String quno = Quantity;
                String productweight = productCommonClass.getProductweight();
                String productimage = productCommonClass.getProductimage();
                String priceid = productCommonClass.getPriceId();
                if (productquantity.equals("0")) {
                    showToast.showInfoToast("Please select product quantity");
                    return;
                }
                if (!prefManager.isLoggedIn()) {
                    logindialog("You have to login first before adding items into Cart ?");
                    return;
                }
                addproducttocart(userid,priceid, productname, productid, productprice, productdisprice, productquantity, quno, productweight, productimage);

                break;
        }
    }

    private void addproducttocart(String userid,String priceid, String productname, String productid, String productprice, String productdisprice, String productquantity, String quno, String productweight, String productimage) {
        spotsDialog.show();
        Ion.with(this)
                .load("POST", BASE_URL + "addproducttocart")
                .setMultipartParameter("userid", userid)
                .setMultipartParameter("priceid", priceid)
                .setMultipartParameter("productid", productid)
                .setMultipartParameter("productname", productname)
                .setMultipartParameter("productprice", productprice)
                .setMultipartParameter("productdiscountprice", productdisprice)
                .setMultipartParameter("productquantity", quno)
                .setMultipartParameter("productquno", productquantity)
                .setMultipartParameter("productweight", productweight)
                .setMultipartParameter("productimage", productimage)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        spotsDialog.dismiss();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg = jsonObject.getString("message");
                            if (error.equals("false")) {
                                showToast.showSuccessToast(msg);
                                badgecount();
                                spotsDialog.dismiss();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            spotsDialog.dismiss();
                        }
                    }
                });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        finish();
    }

    private void logindialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        // Setting Dialog Title
        alertDialog.setTitle("Confirmation For Login ?");
        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, id) -> {
            Intent intent = new Intent(SingleItemActivity.this, LoginActivity.class);
            intent.putExtra("getback", "singleitem");
            startActivityForResult(intent, 3);
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
        prefManager = new PrefManager(this);
        Ion.with(this)
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
                            try{
                                setBadgeCount(this, k);
                            }catch (NullPointerException e1){
                                e1.printStackTrace();
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
