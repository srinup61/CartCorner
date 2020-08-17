package com.intern.kartcorner.ui;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intern.kartcorner.model.Brands;
import com.intern.kartcorner.model.Prices;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.GridAdapter;
import com.intern.kartcorner.helper.BadgeDrawable;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.ProductCommonClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static com.intern.kartcorner.app.Constants.BASE_URL;

public class AllItemsActivity extends AppCompatActivity {
    private ArrayList<ProductCommonClass> arrayList = new ArrayList<ProductCommonClass>();
    private ArrayList<Brands> brandsList = new ArrayList<Brands>();
    private String categoryId,subcatid,uid;
    private RecyclerView productsRecycler;
    private ProgressDailog progressDailog;
    private GridAdapter gridAdapter;
    public static LayerDrawable icon;
    LinearLayout parentlayout;
    private ShowToast showToast;
    private PrefManager prefManager;
    private static String k = null;
    private int page;
    private int code = 0;
    private ArrayList<Prices> prices;
    private LinearLayoutManager layoutManager;
    private static final int MAX_ITEMS_PER_REQUEST = 10;
    private static final int NUMBER_OF_ITEMS = 100;
    private static final int SIMULATED_LOADING_TIME_IN_MS = 1500;
    private ChipGroup chipGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CartCorner");
        prefManager = new PrefManager(this);
        showToast = new ShowToast(this);
        chipGroup = (ChipGroup)findViewById(R.id.brandchips);
        progressDailog = new ProgressDailog(this);
        if(prefManager.getUserId()!=null){
            badgecount();
        }
        prices = new ArrayList<>();
        uid = prefManager.getUserId();
        productsRecycler = findViewById(R.id.allitemsGridvieww);
        layoutManager = new LinearLayoutManager(this);
        productsRecycler.setLayoutManager(layoutManager);
        productsRecycler.setHasFixedSize(true);
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("catid");
        subcatid = intent.getStringExtra("subcatid");
        getProducts(categoryId,subcatid);
        getBrands(categoryId,subcatid);
    }

    private void getBrands(String categoryId, String subcatid) {
        progressDailog.showDailog();
        brandsList.clear();
        Ion.with(this)
                .load("POST",BASE_URL+"getbradnames")
                .setMultipartParameter("categoryid",categoryId)
                .setMultipartParameter("subcategoryid",subcatid)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            progressDailog.dismissDailog();
                            showToast.showWarningToast("Check Your Internet");
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("brands");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Brands brands = new Brands();
                                    brands.setBrandid(jsonObject1.getString("brandid"));
                                    brands.setBrandname(jsonObject1.getString("brandname"));
                                    brands.setCategoryid(jsonObject1.getString("categoryid"));
                                    brands.setSubcategoryid(jsonObject1.getString("subcategoryid"));
                                    brandsList.add(brands);
                                }
                                Brands brands = new Brands("","","Clear","");
                                brandsList.add(brands);
                                setCategoryChips(brandsList);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            progressDailog.dismissDailog();
                        }
                    }
                });
    }

    private void getProducts(String categoryId, String subcatid) {
        arrayList.clear();
        progressDailog.showDailog();
        Ion.with(this)
                .load("POST",BASE_URL+"getproducts")
                .setMultipartParameter("categoryid",categoryId)
                .setMultipartParameter("subcategoryid",subcatid)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            progressDailog.dismissDailog();
                            showToast.showWarningToast("Check Your Internet");
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("products");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    ProductCommonClass productCommonClass = new ProductCommonClass();
                                    productCommonClass.setProductid(jsonObject1.getString("productid"));
                                    productCommonClass.setProductname(jsonObject1.getString("productname"));
                                    Gson gson = new Gson();
                                    String pricesstring = jsonObject1.getString("productprice");
                                    prices = gson.fromJson(pricesstring,new TypeToken<List<Prices>>(){}.getType());
                                    productCommonClass.setPrices(prices);
                                    productCommonClass.setProductimage(jsonObject1.getString("productimage"));
                                    productCommonClass.setProductdesc(jsonObject1.getString("productdesc"));
                                    arrayList.add(productCommonClass);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            progressDailog.dismissDailog();
                            gridAdapter = new GridAdapter(AllItemsActivity.this,arrayList,AllItemsActivity.this);
                            productsRecycler.setAdapter(gridAdapter);
                            productsRecycler.addOnScrollListener(createInfiniteScrollListener());
                            gridAdapter.notifyDataSetChanged();

                            gridAdapter.SetOnItemClickListener((view, position) -> {
                                Intent intent = new Intent(AllItemsActivity.this,SingleItemActivity.class);
                                intent.putExtra("productdata", (Serializable) gridAdapter.mFilteredList.get(position));
                                startActivity(intent);
                            });
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem itemCart = menu.findItem(R.id.cart);
        icon = (LayerDrawable) itemCart.getIcon();
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(this.getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                gridAdapter.getFilter().filter(query);
                gridAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                gridAdapter.getFilter().filter(query);
                gridAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }

    public void setBadgeCount(Context context, String count) {

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
            badgecount();
            icon.mutate();
            icon.setDrawableByLayerId(R.id.ic_badge, badge);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.cart:
                startActivity(new Intent(AllItemsActivity.this, CartItemsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
                            setBadgeCount(AllItemsActivity.this,k);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
    @NonNull private List<ProductCommonClass> getItemsToBeLoaded(int start, int end) throws IndexOutOfBoundsException {
        List<ProductCommonClass> newItems = arrayList.subList(start, end);
        final List<ProductCommonClass> oldItems = ((GridAdapter) Objects.requireNonNull(productsRecycler.getAdapter())).getItems();
        final List<ProductCommonClass> itemsLocal = new LinkedList<>();
        itemsLocal.addAll(oldItems);
        itemsLocal.addAll(newItems);
        return itemsLocal;
    }
    @NonNull
    private InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(MAX_ITEMS_PER_REQUEST, layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {
                simulateLoading();
                int start = ++page * MAX_ITEMS_PER_REQUEST;
                final boolean allItemsLoaded = start >= arrayList.size();
                if (allItemsLoaded) {
                    progressDailog.dismissDailog();
                } else {
                    try{
                        int end = start + MAX_ITEMS_PER_REQUEST;
                        final List<ProductCommonClass> itemsLocal = getItemsToBeLoaded(start, end);
                        refreshView(productsRecycler, new GridAdapter(AllItemsActivity.this,itemsLocal,AllItemsActivity.this), firstVisibleItemPosition);

                    }catch (IndexOutOfBoundsException e){

                    }
                }
            }
        };
    }
    @SuppressLint("StaticFieldLeak")
    private void simulateLoading() {
        new AsyncTask<Void, Void, Void>() {
            @Override protected void onPreExecute() {
                progressDailog.showDailog();
            }

            @Override protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(SIMULATED_LOADING_TIME_IN_MS);
                } catch (InterruptedException e) {
                    Log.e("MainActivity", e.getMessage());
                }
                return null;
            }

            @Override protected void onPostExecute(Void param) {
                progressDailog.dismissDailog();
            }
        }.execute();
    }
    public void setCategoryChips(ArrayList<Brands> categorys) {
        for (Brands category : categorys) {
            Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_category, null, false);
            mChip.setText(category.getBrandname());

            mChip.setOnCheckedChangeListener((compoundButton, b) -> {
                code = 1;
                if(compoundButton.getText() == "Clear"){
                    code = 0;
                    getProducts(categoryId,subcatid);
                    return;
                }
                getBrandProducts(categoryId,subcatid,category.getBrandid());
            });
            chipGroup.addView(mChip);
        }
    }

    private void getBrandProducts(String categoryId, String subcatid, String brandid) {
        arrayList.clear();
        progressDailog.showDailog();
        Ion.with(this)
                .load("POST",BASE_URL+"getbrandproducts")
                .setMultipartParameter("categoryid",categoryId)
                .setMultipartParameter("subcategoryid",subcatid)
                .setMultipartParameter("brandid",brandid)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            progressDailog.dismissDailog();
                            showToast.showWarningToast("Check Your Internet");
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("products");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    ProductCommonClass productCommonClass = new ProductCommonClass();
                                    productCommonClass.setProductid(jsonObject1.getString("productid"));
                                    productCommonClass.setProductname(jsonObject1.getString("productname"));
                                    Gson gson = new Gson();
                                    String pricesstring = jsonObject1.getString("productprice");
                                    prices = gson.fromJson(pricesstring,new TypeToken<List<Prices>>(){}.getType());
                                    productCommonClass.setPrices(prices);
                                    productCommonClass.setProductimage(jsonObject1.getString("productimage"));
                                    productCommonClass.setProductdesc(jsonObject1.getString("productdesc"));
                                    arrayList.add(productCommonClass);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            gridAdapter = new GridAdapter(AllItemsActivity.this,arrayList,AllItemsActivity.this);
                            productsRecycler.setAdapter(gridAdapter);
                            productsRecycler.addOnScrollListener(createInfiniteScrollListener());
                            gridAdapter.notifyDataSetChanged();
                            progressDailog.dismissDailog();
                            gridAdapter.SetOnItemClickListener((view, position) -> {
                                Intent intent = new Intent(AllItemsActivity.this,SingleItemActivity.class);
                                intent.putExtra("productdata", (Serializable) gridAdapter.mFilteredList.get(position));
                                startActivity(intent);
                            });
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(code == 1){
            code = 0;
            getProducts(categoryId,subcatid);
        }else {
            super.onBackPressed();
        }
    }
}
