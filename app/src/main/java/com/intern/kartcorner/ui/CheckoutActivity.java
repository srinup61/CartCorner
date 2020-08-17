package com.intern.kartcorner.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intern.kartcorner.app.Constants;
import com.intern.kartcorner.helper.AlertDialogManager;
import com.intern.kartcorner.model.OffersDAO;
import com.intern.kartcorner.model.OrdersCommonClass;
import com.intern.kartcorner.paytm.Checksum;
import com.intern.kartcorner.paytm.Paytm;
import com.intern.kartcorner.paytm.WebServiceCaller;
import com.intern.kartcorner.services.JavaMailApi;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.AddressesAdapter;
import com.intern.kartcorner.entities.MyAddressDAO;
import com.intern.kartcorner.helper.CalenderSpinnerAdapter;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ShowToast;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import dmax.dialog.SpotsDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class CheckoutActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,CompoundButton.OnCheckedChangeListener{
    @BindView(R.id.choosedate_checkout)
    AppCompatSpinner choosedate;
    @BindView(R.id.choosetime_checkout)
    AppCompatSpinner choosetime;
    @BindView(R.id.checkout_layout)
    NestedScrollView nestedScrollView;
    @BindView(R.id.checkoutsRecycler)
    RecyclerView checkoutsrecyclerView;
    @BindView(R.id.ordersummury)
    LinearLayout linearLayout;
    @BindView(R.id.orderTotalnoofitems)
    TextView orderTotalitems;
    @BindView(R.id.orderDeliveryDate)
    TextView orderDeliveryDate;
    @BindView(R.id.orderDeliveryTime)
    TextView orderDeliveryTime;
    @BindView(R.id.orderDeliveryCharges)
    TextView orderDeliveryCharges;
    @BindView(R.id.ordersavingCost)
    TextView ordersavingCost;
    @BindView(R.id.orderCost)
    TextView orderCost;
    @BindView(R.id.deliveryto_checkout)
    TextView deliverytocheckout;
    @BindView(R.id.deliveryaddress_checkout)
    TextView deliveryaddresscheckout;
    @BindView(R.id.orderDeliverypayamount)
    TextView orderDeliverypayamount;
    @BindView(R.id.cartradiogroup)
    RadioGroup cartradiogroup;
    @BindView(R.id.checkpromoCode)
    AppCompatEditText promoEdittext;
    @BindView(R.id.offersuccesslayout)
    LinearLayout offerSuccess;
    @BindView(R.id.offerfailedlayout)
    LinearLayout offerFail;
    @BindView(R.id.offerfailMessage)
    TextView offerfailMessage;
    @BindView(R.id.offerSuccessMessage)
    TextView offerSuccessMessage;
    @BindView(R.id.submitPromocode)
    AppCompatButton submitPromocode;
    @BindView(R.id.selectPaymenyMode)
    AppCompatSpinner selectPaymenyMode;
    @BindView(R.id.addwalletamount)
    AppCompatCheckBox addwalletamount;
    @BindView(R.id.walletlayout)
    LinearLayout walletlayout;
    @BindView(R.id.showwallet)
    TextView showwallet;
    private String paymentMode,walletamount;
    private PrefManager prefManager;
    private SpotsDialog progressDailog;
    private ShowToast showToast;
    private boolean offerentered = false;
    private String cartType = "", transactionid = "",couponcode = "", address;
    private ArrayList<MyAddressDAO> myAddressDAOS = new ArrayList<>();
    private int status = 0;
    private String totalamount,totalitems,savingamount,deliverydate,deliverytime,products,latlong;
    private GoogleApiClient mGoogleApiClient;
    String cashbackamount = null;
    private int PLACE_PICKER_REQUEST = 1;
    private EditText cityname,arealandmark,areapincode,areaname;
    private int condition = 0;
    private int wallet = 0;
    private AlertDialog alertDialog;
    private String savingCart,offerType,offerPrice,offerPercentage,offerlimitAmount;
    private ArrayList<OrdersCommonClass> cartItems;
    private AlertDialogManager alertDialogManager;
    private OffersDAO offersDAO;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");
        LinearLayoutManager llm = new LinearLayoutManager(this);
        checkoutsrecyclerView.setLayoutManager(llm);
        checkoutsrecyclerView.setHasFixedSize(true);
        cartItems = new ArrayList<>();
        CalenderSpinnerAdapter calenderSpinnerAdapter = new CalenderSpinnerAdapter(this,5);
        choosedate.setAdapter(calenderSpinnerAdapter);
        prefManager = new PrefManager(this);
        progressDailog = new SpotsDialog(this);
        alertDialogManager = new AlertDialogManager();
        showToast = new ShowToast(this);
        try {
            walletamount = prefManager.getWalletAmount();
            wallet = Integer.parseInt(walletamount);
            showwallet.setText(walletamount);
            if(wallet == 0){
                walletlayout.setVisibility(View.GONE);
            }else {
                walletlayout.setVisibility(View.VISIBLE);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }

        getaddresses(prefManager.getUserId());
        Intent intent = getIntent();
        totalamount = intent.getStringExtra("totalamount");
        totalitems = intent.getStringExtra("totalitems");
        savingamount = intent.getStringExtra("savingamount");
        products = intent.getStringExtra("products");
        cartType = intent.getStringExtra("carttype");
        Gson gson = new Gson();
        String totalproducts = intent.getStringExtra("products");
        cartItems = gson.fromJson(totalproducts,new TypeToken<List<OrdersCommonClass>>(){}.getType());
        if(cartType.equals("week") || cartType.equals("month")){
            cartradiogroup.setVisibility(View.GONE);
        }else {
            cartradiogroup.setVisibility(View.VISIBLE);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        orderTotalitems.setText(totalitems);
        orderCost.setText(totalamount);
        ordersavingCost.setText(savingamount);
        if(Integer.parseInt(totalamount)<300){
            orderDeliveryCharges.setText("40");
            int tm = Integer.parseInt(totalamount)+Integer.parseInt("40");
            orderDeliverypayamount.setText(String.valueOf(tm));
            totalamount = String.valueOf(tm);
        }else {
            orderDeliveryCharges.setText("0");
            orderDeliverypayamount.setText(totalamount);
        }

        choosedate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                deliverydate = getSelectedDateAsString(choosedate);
                if(!deliverydate.equals("Choose Date")){
                    orderDeliveryDate.setText(deliverydate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                showToast.showErrorToast("Please Choose Delivery date");
                return;
            }

        });
        choosetime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                deliverytime = choosetime.getSelectedItem().toString();
                if(!deliverytime.equals("Choose TimeSlot")){
                    orderDeliveryTime.setText(deliverytime);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                showToast.showErrorToast("Please Choose Delivery Time slot");
                return;
            }

        });
    }
    @OnClick(R.id.submitPromocode)
    void applyPromoCode(){
            if(Integer.parseInt(totalamount)<=400){
                offerFail.setVisibility(View.VISIBLE);
                return;
            }
            String promocodde = Objects.requireNonNull(promoEdittext.getText()).toString().toUpperCase();
            checkandapplypromocode(promocodde);
    }
    @OnCheckedChanged(R.id.addwalletamount)
    void onGenderSelected(CompoundButton button, boolean checked) {
        if(checked){
            int walletamount = Integer.parseInt(totalamount);
            int finalwaamount = walletamount - wallet;
            totalamount = String.valueOf(finalwaamount);
            orderCost.setText(totalamount);
            orderDeliverypayamount.setText(totalamount);
        }else {
            int walletamount = Integer.parseInt(totalamount);
            int finalwaamount = walletamount + wallet;
            totalamount = String.valueOf(finalwaamount);
            orderCost.setText(totalamount);
            orderDeliverypayamount.setText(totalamount);
        }
    }
    @OnItemSelected(R.id.selectPaymenyMode)
    public void spinnerItemSelected(Spinner spinner, int position) {
        if(position == 0){
            paymentMode = "Choose";
        }
        if(position == 1){
            paymentMode = "COD";
        }
        if(position == 2){
            paymentMode = "PAYTM";
        }
    }

    private void checkandapplypromocode(String promocodde) {
        progressDailog.show();
        Ion.with(this)
                .load("POST",BASE_URL+"checkoffercode")
                .setMultipartParameter("promocode",promocodde)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismiss();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            if(result.toString().contains("[]")){
                                alertDialogManager.showAlertDialog(CheckoutActivity.this,"Offer Invalid !","Hii You have entered Incorrect PromoCode Please Check Once",false);
                                progressDailog.dismiss();
                                return;
                            }
                            JSONObject jsonObject1 = new JSONObject(result);
                            JSONObject jsonObject = jsonObject1.getJSONObject("offerDetails");
                            offersDAO = new OffersDAO();
                            offersDAO.setId(jsonObject.getString("id"));
                            offersDAO.setOfferId(jsonObject.getString("offerId"));
                            offersDAO.setOfferName(jsonObject.getString("offerName"));
                            couponcode = jsonObject.getString("offerCode");
                            offersDAO.setOfferCode(couponcode);
                            offersDAO.setOfferType(jsonObject.getString("offerType"));
                            offerType = jsonObject.getString("offerType");
                            offersDAO.setOfferPrice(jsonObject.getString("offerPrice"));
                            offerPrice = jsonObject.getString("offerPrice");
                            offersDAO.setOfferPercentage(jsonObject.getString("offerPercentage"));
                            offerPercentage = jsonObject.getString("offerPercentage");
                            offersDAO.setOfferNote(jsonObject.getString("offerNote"));
                            offerlimitAmount = jsonObject.getString("offerAmountlimit");
                            offersDAO.setOfferAmountlimit(offerlimitAmount);
                            offersDAO.setOfferValidupto(jsonObject.getString("offerValidupto"));
                            offersDAO.setOfferInserteddate(jsonObject.getString("offerInserteddate"));
                            int offeramount = 0;
                            if(offerType.equals("off")){
                                if(offerPrice.equals("0")){
                                    offeramount = Integer.parseInt(totalamount);
                                    double amount = Double.parseDouble(totalamount);
                                    double res = (amount / 100.0f) * Integer.parseInt(offerPercentage);
                                    totalamount = String.valueOf(res);
                                    offeramount = (int) (offeramount - res);
                                }else {
                                    offeramount = Integer.parseInt(totalamount);
                                    int off = Integer.parseInt(totalamount)- Integer.parseInt(offerPrice);
                                    totalamount = String.valueOf(off);
                                    offeramount = offeramount - off;
                                }
                                orderCost.setText(totalamount);
                                offerSuccess.setVisibility(View.VISIBLE);
                                offerSuccessMessage.setText("Offer Applied And your saved RS."+ offeramount);
                                offerFail.setVisibility(View.GONE);
                                submitPromocode.setEnabled(false);
                                promoEdittext.setEnabled(false);
                                int deliveryCharges = Integer.parseInt(orderDeliveryCharges.getText().toString());
                                int finalAmount = Integer.parseInt(totalamount)+deliveryCharges;
                                totalamount = String.valueOf(finalAmount);
                                orderDeliverypayamount.setText(String.valueOf(totalamount));
                            }else {
                                if(offerPrice.equals("0")){
                                    double amount = Double.parseDouble(totalamount);
                                    double res = (amount / 100.0f) * Integer.parseInt(offerPercentage);
                                    double limitamount = Double.parseDouble(offerlimitAmount);
                                    if(res>limitamount){
                                        res = Double.parseDouble(offerlimitAmount);
                                    }
                                    cashbackamount = String.valueOf(res);
                                }else {
                                    int off = Integer.parseInt(totalamount)- Integer.parseInt(offerPrice);
                                    int limitamount = Integer.parseInt(offerlimitAmount);
                                    if(off>limitamount){
                                        off = Integer.parseInt(offerlimitAmount);
                                    }
                                    cashbackamount = String.valueOf(off);
                                }
                                offerSuccess.setVisibility(View.VISIBLE);
                                offerSuccessMessage.setText("Offer Applied And RS."+cashbackamount+" will credited into your wallet");
                                offerFail.setVisibility(View.GONE);
                                submitPromocode.setEnabled(false);
                                promoEdittext.setEnabled(false);
                            }
                            offerentered = true;
                            progressDailog.dismiss();

                        } catch (JSONException ex) {
                            progressDailog.dismiss();
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void sendAmounttoWallet(String userId, String cashbackamount) {
        progressDailog.show();
        Ion.with(this)
                .load("POST",BASE_URL+"addamounttowallet")
                .setMultipartParameter("userid",userId)
                .setMultipartParameter("cashbackamount",cashbackamount)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        progressDailog.dismiss();
                        showToast.showErrorToast("Something went wrong");
                    } else {
                        progressDailog.dismiss();

                    }
                });
    }

    @OnClick(R.id.placeOrder)
    void placeorder(){
        if(!prefManager.getCity().equals("Vijayawada")){
            alertDialogManager.showAlertDialog(CheckoutActivity.this,"Service Not Available","We are presently not providing Cartcorner services to out of Vijayawada City, Please Stay with us we will touch you soon",false);
            return;
        }
        if(paymentMode.equals("Choose")){
            alertDialogManager.showAlertDialog(CheckoutActivity.this,"Select Payment Type","Please Choose one of Payment Mode",false);
            return;
        }
        if(deliverydate.isEmpty() || deliverydate.equals("Choose Date")){
            alertDialogManager.showAlertDialog(CheckoutActivity.this,"Warning","Please Select Date",false);
            return;
        }
        if(deliverytime.isEmpty() || deliverytime.equals("Choose TimeSlot")){
            alertDialogManager.showAlertDialog(CheckoutActivity.this,"Warning","Please Select Correct Timeslot",false);
            return;
        }
        progressDailog.show();
        if(paymentMode.equals("PAYTM")){
            paytm(totalamount);
        }else {
            saveTransactionDetails();
        }

    }

    private void saveTransactionDetails() {
        String delcharges = null;
        if(Integer.parseInt(totalamount)<=250){
            delcharges = "40";
        }else {
            delcharges = "0";
        }
        progressDailog.show();
        Ion.with(this)
                .load("POST",BASE_URL+"placeorder")
                .setMultipartParameter("userid",prefManager.getUserId())
                .setMultipartParameter("username",prefManager.getName())
                .setMultipartParameter("userphone",prefManager.getMobileNumber())
                .setMultipartParameter("useremail",prefManager.getEmail())
                .setMultipartParameter("deliverydate",deliverydate)
                .setMultipartParameter("deliverytime",deliverytime)
                .setMultipartParameter("totalamount",totalamount)
                .setMultipartParameter("deliverycharges",delcharges)
                .setMultipartParameter("discountamount",savingamount)
                .setMultipartParameter("paymentmethod",paymentMode)
                .setMultipartParameter("products",products)
                .setMultipartParameter("carttype",cartType)
                .setMultipartParameter("transactionid",transactionid)
                .setMultipartParameter("couponcode",couponcode)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismiss();
                        showToast.showErrorToast("Something went wrong");
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg = jsonObject.getString("message");
                            if(error.equals("false")){
                                showToast.showSuccessToast(msg);
                                if(offerentered){
                                    sendAmounttoWallet(prefManager.getUserId(),cashbackamount);
                                }
                                if(wallet!=0){
                                    clearwallet(prefManager.getUserId());
                                }
                                sendEmail();
                                final OkHttpClient client=new OkHttpClient();
                                String  url = "https://www.smsgatewayhub.com/api/mt/SendSMS?APIKey=qO3Yw24kVkepMeKVkhXkIA&senderid=CRTCNR&channel=2&DCS=0&flashsms=0&" +
                                        "number="+"91"+prefManager.getMobileNumber()+"&text="+"Cart Corner has received Your Order and Cartcorner representative will deliver  by "+deliverydate+" - "+deliverytime +"\n"+ "Thank You"+"&route=1";
                                Request request=new Request.Builder()
                                        .url(url)
                                        .build();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(okhttp3.Call call, IOException e) {
                                        // Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                        if(response.isSuccessful())
                                        {
                                            Intent intent = new Intent(CheckoutActivity.this,ThankyouActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }else {
                                showToast.showErrorToast(msg);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }

    private void clearwallet(String userId) {
        Ion.with(this)
                .load("POST",BASE_URL+"clearwalletamount")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){

                    }else {

                    }
                });
    }

    private void sendEmail() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Hi "+ prefManager.getName() +" Your order Has been Placed Successfully and CartCorner will deleivery on "+deliverydate +"-"+deliverytime +"\n\n");
        stringBuffer.append("Product Details" +"\n");
        for (int i=0;i<cartItems.size();i++){
            stringBuffer.append(cartItems.get(i).getProductname() +" : "+cartItems.get(i).getProductquno()+"*"+cartItems.get(i).getProductweight().substring(1) +" - "+ cartItems.get(i).getProductdisprice() + "\n");
        }
        if(!transactionid.isEmpty()){
            stringBuffer.append("Transaction Id : "+ transactionid +"\n");
        }
        if(!couponcode.isEmpty()){
            stringBuffer.append("Applied Coupon Code : "+ couponcode +"\n");
        }
        stringBuffer.append("Payment Mode : "+paymentMode +"\n");
        stringBuffer.append("Total Order Amount is :" + totalamount +"\n\n");
        stringBuffer.append("User Details" +"\n");
        stringBuffer.append("User Name : "+ prefManager.getName() +"\n");
        stringBuffer.append("User Email : "+ prefManager.getEmail() +"\n");
        stringBuffer.append("User Mobile Number : "+ prefManager.getMobileNumber() +"\n");
        stringBuffer.append("User Address : "+ deliveryaddresscheckout.getText().toString());
        String email= prefManager.getEmail();
        String message=stringBuffer.toString();
        String subject="Order Confirmation From Cart Corner";
        JavaMailApi javaMailApi = new JavaMailApi(CheckoutActivity.this,email,subject,message);
        javaMailApi.execute();
    }
    private void paytm(String totalamount) {
        String custID = prefManager.getUserId();
        String orderID = generateString();
        String callBackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID"+orderID;

        final Paytm paytm = new Paytm(
                "GrUFRx39482429291340",
                "WAP",
                totalamount,
                "DEFAULT",
                callBackurl,
                "Retail",
                orderID,
                custID
        );
        WebServiceCaller.getClient().getChecksum(paytm.getmId(), paytm.getOrderId(), paytm.getCustId()
                , paytm.getChannelId(), paytm.getTxnAmount(), paytm.getWebsite(), paytm.getCallBackUrl(), paytm.getIndustryTypeId()
        ).enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                if (response.isSuccessful()) {
                    processToPay(response.body().getChecksumHash(),paytm);
                    progressDailog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {
                progressDailog.dismiss();

            }
        });
    }
    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
    @OnClick({R.id.week, R.id.month})
    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.week:
                if (checked) {
                    savingCart = "week";
                    additemtocart(prefManager.getUserId(),savingCart,cartItems);
                }
                break;
            case R.id.month:
                if (checked) {
                    // 2 clicked
                    savingCart = "month";
                    additemtocart(prefManager.getUserId(),savingCart,cartItems);
                }
                break;
        }
    }

    private void additemtocart(String userId, String savingCart, ArrayList<OrdersCommonClass> cartItems) {
        AlertDialog alertbox = new AlertDialog.Builder(CheckoutActivity.this,R.style.MyAlertDialogStyle)
                .setTitle("Confirmation For Weekly Cart ?")
                .setMessage("Do you want to add this items into "+ savingCart +"?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    progressDailog.show();
                    placecartOrders(userId,savingCart,cartItems);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void placecartOrders(String userId, String savingCart, ArrayList<OrdersCommonClass> cartItems) {
            for(int i = 0; i<cartItems.size();i++){
                String userid = userId;
                String priceid = cartItems.get(i).getPriceid();
                String productid = cartItems.get(i).getProductid();
                String productname = cartItems.get(i).getProductname();
                String productprice = cartItems.get(i).getProductprice();
                String productdprice = cartItems.get(i).getProductdisprice();
                String productunitcost = "";
                String productquantity = cartItems.get(i).getProductquantity();
                String productweight = cartItems.get(i).getProductweight();
                String productimage = cartItems.get(i).getProductimage();
                String productquno = cartItems.get(i).getProductquno();
                String cartType;
                if(savingCart.equals("week")){
                    cartType = "addproducttoweeklycart";
                }else {
                    cartType = "addproducttomonthlycart";
                }
                addproduct(cartType,userid,priceid,productid,productname,productprice,productdprice,productunitcost,productquantity,productweight,productimage,productquno);
            }
    }

    private void addproduct(String cartURL,String userid, String priceid, String productid, String productname, String productprice, String productdprice, String productunitcost, String productquantity, String productweight, String productimage, String productquno) {
        Ion.with(CheckoutActivity.this)
                .load("POST", BASE_URL + cartURL)
                .setMultipartParameter("userid", userid)
                .setMultipartParameter("priceid", priceid)
                .setMultipartParameter("productid", productid)
                .setMultipartParameter("productname", productname)
                .setMultipartParameter("productprice", productprice)
                .setMultipartParameter("productdiscountprice", productdprice)
                .setMultipartParameter("productquantity", productquno)
                .setMultipartParameter("productquno", productquantity)
                .setMultipartParameter("productweight", productweight)
                .setMultipartParameter("productimage", productimage)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg = jsonObject.getString("message");
                            if (error.equals("false")) {
                                if(savingCart.equals("week")){
                                    showToast.showSuccessToast("This cart Items Added Successfully into Weekly Cart");
                                }else {
                                    showToast.showSuccessToast("This cart Items Added Successfully into Monthly Cart");
                                }

                            } else {

                            }
                            progressDailog.dismiss();
                        } catch (JSONException e1) {
                            progressDailog.dismiss();
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.changeaddress_checkout)
    void changeaddress(){
        showaddaddresslayout();
    }
    public void getaddresses(String userId) {
        progressDailog.show();
        Ion.with(this)
                .load("POST",BASE_URL+"getaddresses")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismiss();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            myAddressDAOS.clear();
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("myaddresses");
                            if(jsonArray.length()==0){
                                showaddressdailog();
                                progressDailog.dismiss();
                                showToast.showErrorToast("You don't have addresses You should add one Delivery Address");
                                return;
                            }
                            for (int i=0;i<jsonArray.length();i++){
                                MyAddressDAO myAddressDAO = new MyAddressDAO();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                myAddressDAO.setAddressid(jsonObject1.getString("addressid"));
                                myAddressDAO.setAddress(jsonObject1.getString("address"));
                                myAddressDAO.setAddressnickname(jsonObject1.getString("addressnickname"));
                                myAddressDAO.setPersonname(jsonObject1.getString("personname"));
                                myAddressDAO.setDefaulttype(jsonObject1.getString("defaulttype"));
                                if(jsonObject1.getString("defaulttype").equals("1")){
                                    deliverytocheckout.setText(jsonObject1.getString("addressnickname"));
                                    deliveryaddresscheckout.setText(jsonObject1.getString("address"));
                                }
                                myAddressDAO.setLatlong(jsonObject1.getString("latlong"));
                                myAddressDAOS.add(myAddressDAO);
                            }
                            Collections.reverse(myAddressDAOS);
                            AddressesAdapter addressesAdapter = new AddressesAdapter(CheckoutActivity.this,myAddressDAOS, CheckoutActivity.this,2);
                            checkoutsrecyclerView.setAdapter(addressesAdapter);
                            addressesAdapter.notifyDataSetChanged();
                            progressDailog.dismiss();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void showaddressdailog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.addaddress_layout, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        EditText nickname = dialogView.findViewById(R.id.ch_addressnicknameedit);
        EditText personname = dialogView.findViewById(R.id.ch_addresspersonnameedit);
        EditText houseno = dialogView.findViewById(R.id.ch_addresshousenoedit);

        EditText streetname = dialogView.findViewById(R.id.ch_addressstreetedit);
        cityname = dialogView.findViewById(R.id.ch_addresscityedit);
        areaname = dialogView.findViewById(R.id.ch_addressareaedit);

        EditText apartmentname = dialogView.findViewById(R.id.ch_addressapartmentnameedit);
        arealandmark = dialogView.findViewById(R.id.ch_addresslandmarkedit);
        areapincode = dialogView.findViewById(R.id.ch_addresspincodeedit);

        TextView manual = dialogView.findViewById(R.id.ch_selectmanually);
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(CheckoutActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

            }
        });

        CheckBox confircheckbox = dialogView.findViewById(R.id.ch_default_type);
        confircheckbox.setOnCheckedChangeListener(this);
        Button save = dialogView.findViewById(R.id.ch_saveAddress);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDailog.show();
                String nickName = nickname.getText().toString();
                String personName = personname.getText().toString();
                String houseNo = houseno.getText().toString();
                String streetName = streetname.getText().toString();
                String area = areaname.getText().toString();
                String apartmentName = apartmentname.getText().toString();
                String landMark = arealandmark.getText().toString();
                String pinCode = areapincode.getText().toString();
                String city = cityname.getText().toString();
                if(nickName.isEmpty()||personName.isEmpty()||landMark.isEmpty()||pinCode.isEmpty()||city.isEmpty()){
                    showToast.showInfoToast("Should be fill Nick name,Person Name,Pincode,City, Landmark");
                    progressDailog.dismiss();
                    return;
                }
                StringBuilder addressstring = new StringBuilder();
                if(!houseNo.isEmpty()){
                    addressstring.append(houseNo);
                    addressstring.append(",");
                }
                if(!streetName.isEmpty()){
                    addressstring.append(streetName);
                    addressstring.append(",");
                }
                if(!apartmentName.isEmpty()){
                    addressstring.append(apartmentName);
                    addressstring.append(",");
                }
                addressstring.append(area);
                addressstring.append(",");
                addressstring.append(landMark);
                addressstring.append(",");
                addressstring.append(city);
                addressstring.append(",");
                addressstring.append(pinCode);
                address = addressstring.toString();
                addAddress(prefManager.getUserId(),nickName,personName,address,condition,latlong);

            }
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            condition = 1;
        }else {
            condition = 0;
        }
    }
    private void
    showaddaddresslayout() {
        status = 1;
        View forgotLayout = findViewById(R.id.checkoutsRecycler);
        forgotLayout.setAnimation(AnimationUtils.makeInChildBottomAnimation(this));
        forgotLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> findViewById(R.id.checkout_layout).setVisibility(View.GONE), 500);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    private void recyclershow(){
        status = 0;
        View forgotLayout = findViewById(R.id.checkout_layout);
        forgotLayout.setAnimation(AnimationUtils.makeInChildBottomAnimation(this));
        forgotLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> findViewById(R.id.checkoutsRecycler).setVisibility(View.GONE), 500);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            recyclershow();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private String getSelectedDateAsString(Spinner dateSpinner) {
        Calendar selectedDate = (Calendar) dateSpinner.getSelectedItem();
        return new SimpleDateFormat("dd MMM yyyy").format(selectedDate.getTimeInMillis());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                latlong = latitude + "," + longitude;
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(
                            place.getLatLng().latitude,
                            place.getLatLng().longitude,
                            1);
                } catch (Exception ioException) {
                    Log.e("", "Error in getting address for the location");
                }
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String pincode = String.valueOf(address.getPostalCode());
                    String sublocality = String.valueOf(address.getSubLocality());
                    String landmark = String.valueOf(address.getAdminArea());
                    String city = String.valueOf(address.getLocality());
                    cityname.setText(city);
                    areapincode.setText(pincode);
                    areaname.setText(sublocality);
                    arealandmark.setText(landmark);
                }
            }
        }
    }
    private void addAddress(String userId, String addressnickname, String personName, String address, int condition, String latlong) {
        Ion.with(this)
                .load("POST",BASE_URL+"addaddress")
                .setMultipartParameter("userid",userId)
                .setMultipartParameter("defaulttype",String.valueOf(condition))
                .setMultipartParameter("address",address)
                .setMultipartParameter("addressnickname",addressnickname)
                .setMultipartParameter("personname",personName)
                .setMultipartParameter("latlong",latlong)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismiss();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg  = jsonObject.getString("message");
                            if(error.equals("false")){
                                showToast.showSuccessToast(msg);
                                recyclershow();
                                getaddresses(prefManager.getUserId());
                                alertDialog.dismiss();

                            }else {
                                showToast.showWarningToast(msg);
                            }
                            progressDailog.dismiss();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            progressDailog.dismiss();
                        }
                    }
                });
    }
    private void processToPay(String checksumHash, Paytm paytm) {
        PaytmPGService Service = PaytmPGService.getProductionService();

        HashMap<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , paytm.getmId());
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , paytm.getOrderId());
        paramMap.put( "CUST_ID" , paytm.getCustId());
        paramMap.put( "CHANNEL_ID" , paytm.getChannelId());
        paramMap.put( "TXN_AMOUNT" , paytm.getTxnAmount());
        paramMap.put( "WEBSITE" , paytm.getWebsite());
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , paytm.getIndustryTypeId());
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put( "CHECKSUMHASH" , checksumHash);
        PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {}
            public void onTransactionResponse(Bundle inResponse) {
                // Toast.makeText(PaytmPage.this, inResponse.toString(), Toast.LENGTH_SHORT).show();
                if(inResponse.toString().contains("TXN_SUCCESS") || inResponse.toString().contains("Success"))
                {
                    Toast.makeText(CheckoutActivity.this, " Transaction Successful", Toast.LENGTH_SHORT).show();
                    transactionid = inResponse.getString("TXNID");
//                    stringBuffer.append("\n"+"Payment Mode:- "+paymentmode+"\n");
//                    stringBuffer.append("\n"+"Delivery Time:-"+deliveryp+"\n");
//                    sendMail(emailid1.trim(),stringBuffer);
                    //      sendMail("surajkumar13991399@gmail.com",stringBuffer);
                    saveTransactionDetails();
                }
                else
                {
                    Toast.makeText(CheckoutActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                }


            }
            public void networkNotAvailable() {}
            public void clientAuthenticationFailed(String inErrorMessage) {}
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {}
            public void onBackPressedCancelTransaction() {}
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {}
        });

    }
}
