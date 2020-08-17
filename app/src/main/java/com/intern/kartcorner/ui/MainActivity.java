package com.intern.kartcorner.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.intern.kartcorner.fragments.AboutUsFragment;
import com.intern.kartcorner.fragments.CancelandReturnFragment;
import com.intern.kartcorner.fragments.TermsandConditionFragment;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.app.ConnectivityReceiver;
import com.intern.kartcorner.fragments.ContactUsFragment;
import com.intern.kartcorner.fragments.DashboardFragment;
import com.intern.kartcorner.fragments.MyAddressesFragment;
import com.intern.kartcorner.fragments.MyOrdersFragment;
import com.intern.kartcorner.helper.AlertDialogManager;
import com.intern.kartcorner.helper.BadgeDrawable;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.model.ChildModel;
import com.intern.kartcorner.model.HeaderModel;
import com.intern.kartcorner.view.ExpandableNavigationListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ExpandableNavigationListView navigationExpandableListView;
    public PrefManager prefManager;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private AlertDialogManager alertDialogManager;
    public static int navItemIndex = 0;
    public static LayerDrawable icon;
    private static String k = null;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alertDialogManager = new AlertDialogManager();
        if(!ConnectivityReceiver.isConnected()){
            alertDialogManager.showAlertDialog1(this,"No Internet Connection","",false);
            return;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationExpandableListView = (ExpandableNavigationListView) findViewById(R.id.expandable_navigation);
        NavigationView navigationView = findViewById(R.id.nav_view);
        prefManager = new PrefManager(this);
        if(getIntent().hasExtra("getback")){
            Fragment fragment = new MyAddressesFragment();
            loadFragment(fragment);
            navItemIndex = 5;
        }
        if(prefManager.getUserId()!=null){
            badgecount();
        }
        initialviews(navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        loadFragment(new DashboardFragment());
        navigationExpandableListView
                .init(this)
                .addHeaderModel(new HeaderModel("Dashboard",R.drawable.home))
                .addHeaderModel(new HeaderModel("My Carts", R.drawable.cart, true)
                        .addChildModel(new ChildModel("Shopping Cart"))
                        .addChildModel(new ChildModel("Weekly Cart"))
                        .addChildModel(new ChildModel("Monthly Cart")))

                .addHeaderModel(new HeaderModel("My Profile",R.drawable.myaccount))
                .addHeaderModel(new HeaderModel("Order History",R.drawable.myorders))
                .addHeaderModel(new HeaderModel("My Addresses",R.drawable.myaddress))
                .addHeaderModel(new HeaderModel("Cancel And Return Policy",R.drawable.ic_assignment_return_black_24dp))
                .addHeaderModel(new HeaderModel("Terms And Condition",R.drawable.ic_comment_black_24dp))
                .addHeaderModel(new HeaderModel("About Us",R.drawable.aboutus))
                .addHeaderModel(new HeaderModel("Contact Us",R.drawable.phone))
                .addHeaderModel(new HeaderModel("Rate Us",R.drawable.ic_star_black_24dp))
                .addHeaderModel(new HeaderModel("Share",R.drawable.ic_menu_share))
                .addHeaderModel(new HeaderModel("Logout",R.drawable.signout))
                .build()
                .addOnGroupClickListener((parent, v, groupPosition, id) -> {
                    navigationExpandableListView.setSelected(groupPosition);
                    Fragment fragment;
                    //drawer.closeDrawer(GravityCompat.START);
                    if (id == 0) {
                        //Home Menu
                        fragment = new DashboardFragment();
                        loadFragment(fragment);
                        navItemIndex = 0;
                        drawer.closeDrawer(GravityCompat.START);
                    } else if (id == 1) {
                        //Cart Menu
                        navItemIndex = 1;
                    } else if (id == 2) {
                        if(prefManager.isLoggedIn()){
                            //Cart Menu
                            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                            startActivity(intent);
                            navItemIndex = 2;
                        }else {
                            logindialog("You have to login first before viewing your Profile ?");
                        }
                        drawer.closeDrawer(GravityCompat.START);
                    }  else if (id == 3) {
                        //Orders Menu
                        if(prefManager.isLoggedIn()){
                            //Cart Menu
                            fragment = new MyOrdersFragment();
                            loadFragment(fragment);
                            navItemIndex = 3;
                        }else {
                            logindialog("You have to login first before viewing your Orders ?");
                        }
                        drawer.closeDrawer(GravityCompat.START);
                    } else if (id == 4) {
                        if(prefManager.isLoggedIn()){
                            //Notifications Menu
                            fragment = new MyAddressesFragment();
                            loadFragment(fragment);
                            navItemIndex = 4;
                        }else {
                            logindialog("You have to login first before viewing your Addresses ?");
                        }
                        drawer.closeDrawer(GravityCompat.START);
                    } else if (id == 5) {
                        fragment = new CancelandReturnFragment();
                        loadFragment(fragment);
                        navItemIndex = 5;
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    else if (id == 6) {
                        fragment = new TermsandConditionFragment();
                        loadFragment(fragment);
                        navItemIndex = 6;
                        drawer.closeDrawer(GravityCompat.START);
                    }else if (id == 7) {
                        fragment = new AboutUsFragment();
                        loadFragment(fragment);
                        navItemIndex = 7;
                        drawer.closeDrawer(GravityCompat.START);
                    } else if (id == 8) {
                        fragment = new ContactUsFragment();
                        loadFragment(fragment);
                        navItemIndex = 8;
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    else if (id == 9) {
                        String url36 = "https://play.google.com/store/apps/details?id=com.intern.kartcorner";
                        Intent i36 = new Intent(Intent.ACTION_VIEW);
                        i36.setData(Uri.parse(url36));
                        startActivity(i36);
                        navItemIndex = 9;
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    else if (id == 10) {
//                        Intent sendIntent = new Intent();
//                        sendIntent.setAction(Intent.ACTION_SEND);
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Wanna Order Grocery Online." + "\n" + "Download The App Now" + "\n" + "https://play.google.com/store/apps/details?id=com.intern.kartcorner&hl=en_IN");
//                        sendIntent.setType("text/plain");
//                        sendIntent.setPackage("com.whatsapp");
//                        startActivity(sendIntent);

                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                        // Add data to the intent, the receiving app will decide
                        // what to do with it.
                        share.putExtra(Intent.EXTRA_SUBJECT, "CartCorner");
                        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.intern.kartcorner&hl=en_IN");

                        startActivity(Intent.createChooser(share, "Share link!"));
                        navItemIndex = 7;
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    else if (id == 11) {
                        loginback();
                        navItemIndex = 8;
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    return false;
                })
                .addOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                    navigationExpandableListView.setSelected(groupPosition, childPosition);
                    //Toast.makeText(MainActivity.this,"Under Construction",Toast.LENGTH_LONG).show();
                    String catid = null;
                    String catname = null;
                    if (id == 0) {
                       catid = "301";
                       catname = "cart";
                    } else if (id == 1) {
                        catid = "302";
                        catname = "week";
                    } else if (id == 2) {
                        catid = "303";
                        catname = "month";
                    }
                    if(prefManager.isLoggedIn()){
                        //Cart Menu
                        if(catname.equals("cart")){
                            Intent intent = new Intent(MainActivity.this,CartItemsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MainActivity.this,WMCartActivity.class);
                            intent.putExtra("carttype",catname);
                            startActivity(intent);
                        }

                        navItemIndex = 0;
                    }else {
                        logindialog("You have to login first before viewing your Cart Items?");
                    }
//                    Intent intent = new Intent(MainActivity.this, SubCategoriesActivity.class);
//                    intent.putExtra("catid",catid);
//                    intent.putExtra("catname",catname);
//                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                });
        navigationExpandableListView.expandGroup(2);
    }

    private void initialviews(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView profilename = headerView.findViewById(R.id.profile_name);
        profilename.setText(prefManager.getName());
        TextView profilmail = headerView.findViewById(R.id.profile_mail);
        profilmail.setText(prefManager.getEmail());
        CircleImageView profilepic = headerView.findViewById(R.id.profile_pic);
        String profileurl = prefManager.getprofilepic();
        if(profileurl!=null){
            Picasso.with(MainActivity.this).load(prefManager.getprofilepic()).fit().centerCrop().into(profilepic);
        }
        profilename.setOnClickListener(view -> {
            if(prefManager.isLoggedIn()){
                //Cart Menu
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
                navItemIndex = 3;
            }else {
                logindialog("You have to login first before viewing your Profile ?");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        icon = (LayerDrawable) itemCart.getIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            if(prefManager.isLoggedIn()){
                Intent intent = new Intent(MainActivity.this,CartItemsActivity.class);
                startActivity(intent);

            }else {
                logindialog("You have to login first before viewing your Cart Items?");
            }

            return true;
        }
        if(id == R.id.offers){
            Intent intent = new Intent(MainActivity.this,OffersActivity.class);
            startActivity(intent);
        }
        if(id == R.id.weekly_cart){
            Intent intent = new Intent(MainActivity.this,WMCartActivity.class);
            intent.putExtra("carttype","week");
            startActivity(intent);
        }
        if(id == R.id.monthly_cart){
            Intent intent = new Intent(MainActivity.this,WMCartActivity.class);
            intent.putExtra("carttype","month");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    protected void loginback() {

        AlertDialog alertbox = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                .setMessage("Do you want to Confirm the Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        prefManager.clearSession();
                        Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(logout);
                        finish();
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                navigationExpandableListView.setSelected(0);
                Fragment fragment = new DashboardFragment();
                loadFragment(fragment);
                return;
            }
        }else {
            super.onBackPressed();
        }
        if(shouldLoadHomeFragOnBackPress){
            exitByBackKey();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                        //close();


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

    private void logindialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle).create();
        // Setting Dialog Title
        alertDialog.setTitle("Confirmation For Login ?");
        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, id) -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("getback","main");
            startActivityForResult(intent,4);
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
    public static void setBadgeCount(Context context, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        try {
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
            if (reuse != null && reuse instanceof BadgeDrawable) {
                badge = (BadgeDrawable) reuse;
            } else {
                badge = new BadgeDrawable(context);
            }

            badge.setCount(count);
            icon.mutate();
            icon.setDrawableByLayerId(R.id.ic_badge, badge);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    public void badgecount(){
        prefManager = new PrefManager(this);
        Ion.with(this)
                .load("POST",BASE_URL+"getcartitems")
                .setMultipartParameter("userid",prefManager.getUserId())
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){

                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("cartitems");
                            k = String.valueOf(jsonArray.length());
                            setBadgeCount(this,k);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PrefManager prefManager = new PrefManager(this);
        getWalletAmount(prefManager.getUserId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefManager prefManager = new PrefManager(this);
        getWalletAmount(prefManager.getUserId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefManager prefManager = new PrefManager(this);
        getWalletAmount(prefManager.getUserId());
    }

    private void getWalletAmount(String userId) {
        Ion.with(this)
                .load("POST",BASE_URL+"getwalletamount")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){

                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("walletdetails");
                            prefManager.setWalletAmount(jsonObject1.getString("walletamount"));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }
}
