package com.intern.kartcorner.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intern.kartcorner.adapters.AutoCompleteAdapter;
import com.intern.kartcorner.model.Prices;
import com.intern.kartcorner.model.ProductCommonClass;
import com.intern.kartcorner.ui.SingleItemActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.CategoryAdapter;
import com.intern.kartcorner.helper.GridSpacingItemDecoration;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.CategoriesDAO;
import com.intern.kartcorner.ui.AllItemsActivity;
import com.intern.kartcorner.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class DashboardFragment extends Fragment implements BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener,View.OnClickListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SliderLayout mDemoSlider,lowerslider;
    HashMap<String, String> HashMapForURL,lHashMapForURL ;
    private OnFragmentInteractionListener mListener;
    private ArrayList<ProductCommonClass> arrayList = new ArrayList<ProductCommonClass>();
    private ArrayList<String> productNames = new ArrayList<>();
    public static LayerDrawable icon;
    @BindView(R.id.categorie_recyclerView)
    RecyclerView catRecyclerview;
    private ArrayList<Prices> prices;
    private SpotsDialog progressDailog;
    private ShowToast showToast;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<CategoriesDAO> categoriesDAOS = new ArrayList<>();
    public DashboardFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,view);
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);
        lowerslider = (SliderLayout)view.findViewById(R.id.lowerslider);
        autoCompleteTextView = view.findViewById(R.id.productsACT);
        prices = new ArrayList<>();
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        initViews();
        return view;
    }

    private void getCategories() {
        progressDailog.show();
        Ion.with(this)
                .load("GET",BASE_URL+"getcategories")
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismiss();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("categories");
                            for(int i=0;i<jsonArray.length();i++){
                                CategoriesDAO categoriesDAO = new CategoriesDAO();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                categoriesDAO.setCategoryId(jsonObject1.getString("categoryid"));
                                categoriesDAO.setCategoryname(jsonObject1.getString("categoryname"));
                                categoriesDAO.setCategoryimage(jsonObject1.getString("categoryimage"));
                                categoriesDAOS.add(categoriesDAO);
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(),categoriesDAOS);
                            catRecyclerview.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                            progressDailog.dismiss();
                        } catch (JSONException e1) {
                            progressDailog.dismiss();
                            e1.printStackTrace();
                        }

                    }
                });

    }

    private void initViews() {

        if(collapsingToolbarLayout != null){
            collapsingToolbarLayout.setTitle("CartCorner");
            //collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }
        progressDailog = new SpotsDialog(getContext());
        showToast = new ShowToast(getContext());
        //Call this method if you want to add images from URL .
        @SuppressLint("WrongConstant")
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        catRecyclerview.setLayoutManager(manager);
        int spanCount = 2; // 3 columns
        int spacing = 10; // 50px
        boolean includeEdge = false;
        catRecyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        getSlides();
        getlowerSlides();
        getCategories();
    }

    private void getlowerSlides() {
        lHashMapForURL = new HashMap<String, String>();
        Ion.with(getContext())
                .load(BASE_URL+"getlowerslides")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){

                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("banners");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String image = jsonObject1.getString("bannerurl");
                                    String info = jsonObject1.getString("bannerinfo");
                                    lHashMapForURL.put(info, image);
                                }
                                showslider();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        slider.getView();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    public void AddImagesUrlOnline(){
//
//        HashMapForURL = new HashMap<String, String>();
//        HashMapForURL.put("FreshKart", "http://freshcart.info/Freshkart/productimage/slider1.jpg");
//        HashMapForURL.put("Beverges", "http://freshcart.info/Freshkart/productimage/slider2.jpg");
//        HashMapForURL.put("Fruits&Vegetables", "http://freshcart.info/Freshkart/productimage/slider3.jpg");
//        HashMapForURL.put("Sweets&Backery", "http://freshcart.info/Freshkart/productimage/slider4.jpg");
//    }
    private void showslider() {
        for(String name : lHashMapForURL.keySet()){

            TextSliderView textSliderView = new TextSliderView(getContext());

            textSliderView
                    .description("")
                    .image(lHashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            lowerslider.addSlider(textSliderView);
        }
        lowerslider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        lowerslider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        lowerslider.setCustomAnimation(new DescriptionAnimation());
        lowerslider.setDuration(5000);
        lowerslider.addOnPageChangeListener(this);
    }
    private void showlowerslider() {
        for(String name : HashMapForURL.keySet()){

            TextSliderView textSliderView = new TextSliderView(getContext());

            textSliderView
                    .description("")
                    .image(HashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);
        mDemoSlider.addOnPageChangeListener(this);
    }
    private void getSlides() {
        HashMapForURL = new HashMap<String, String>();
        Ion.with(getContext())
                .load(BASE_URL+"getslides")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){

                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("banners");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String image = jsonObject1.getString("bannerurl");
                                    String info = jsonObject1.getString("bannerinfo");
                                    HashMapForURL.put(info, image);
                                }
                                showlowerslider();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                });
    }

//private void AddImagesUrlOnline() {
//    HashMapForURL = new HashMap<String, String>();
//    for(int i=0;i<slideimages.size();i++){
//        HashMapForURL.put(String.valueOf(i), slideimages.get(i));
//    }
//}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.homefragmentmenu, menu);
        MenuItem itemCart = menu.findItem(R.id.cartt);
        icon = (LayerDrawable) itemCart.getIcon();
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cartt:
                startActivity(new Intent(getContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllItems();
    }

    private void getAllItems() {
        progressDailog.show();
        Ion.with(this)
                .load("GET",BASE_URL+"getAllproducts")
                .asString()
                .setCallback((FutureCallback<String>) (e, result) -> {
                    if(e!=null){
                        progressDailog.dismiss();
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
                                String pricesstring = jsonObject1.getString("productprice");
                                productCommonClass.setProductprice(pricesstring);
                                Gson gson = new Gson();
                                prices = gson.fromJson(pricesstring,new TypeToken<List<Prices>>(){}.getType());
                                productCommonClass.setPrices(prices);
                                productCommonClass.setProductimage(jsonObject1.getString("productimage"));
                                productCommonClass.setProductdesc(jsonObject1.getString("productdesc"));
                                arrayList.add(productCommonClass);
                                productNames.add(jsonObject1.getString("productname"));
                            }
                            AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, productNames, arrayList);
                            autoCompleteTextView.setAdapter(adapter);
                            autoCompleteTextView.setThreshold(1);
                            adapter.notifyDataSetChanged();
                            progressDailog.dismiss();
                            autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
                                Intent intent = new Intent(getActivity(), SingleItemActivity.class);
                                intent.putExtra("productdata", (Serializable) arrayList.get(i));
                                startActivity(intent);
                                autoCompleteTextView.setText("");
                            });
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        progressDailog.dismiss();
                    }
                });
    }
}
