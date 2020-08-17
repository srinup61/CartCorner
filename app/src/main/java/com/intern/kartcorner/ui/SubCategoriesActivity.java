package com.intern.kartcorner.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.SubCatAdapter;
import com.intern.kartcorner.helper.AlertDialogManager;
import com.intern.kartcorner.helper.GridSpacingItemDecoration;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.SubCategoriesDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class SubCategoriesActivity extends AppCompatActivity {
    private RecyclerView subcatrecycler;
    private AlertDialogManager alertDialogManager;
    private ProgressDailog progressDailog;
    private ShowToast showToast;
    private ArrayList<SubCategoriesDAO> subCategoriesDAOS = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitViews();
        Intent intent = getIntent();
        String catid = intent.getStringExtra("catid");
        String catname = intent.getStringExtra("catname");
        getSupportActionBar().setTitle(catname);
        getsubCategories(catid);
    }

    private void getsubCategories(String catid) {
        progressDailog.showDailog();
        Ion.with(this)
                .load("POST",BASE_URL+"getsubcategories")
                .setMultipartParameter("categoryid",catid)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            progressDailog.dismissDailog();
                            showToast.showErrorToast("Something went wrong");
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("subcategories");
                                for(int i=0;i<jsonArray.length();i++){
                                    SubCategoriesDAO categoriesDAO = new SubCategoriesDAO();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    categoriesDAO.setCategoryid(jsonObject1.getString("categoryid"));
                                    categoriesDAO.setSubcategoryid(jsonObject1.getString("subcategoryid"));
                                    categoriesDAO.setSubcategoryname(jsonObject1.getString("subcategoryname"));
                                    categoriesDAO.setSubcategoryimage(jsonObject1.getString("subcategoryimage"));
                                    subCategoriesDAOS.add(categoriesDAO);
                                }
                                SubCatAdapter categoryAdapter = new SubCatAdapter(SubCategoriesActivity.this,subCategoriesDAOS);
                                subcatrecycler.setAdapter(categoryAdapter);
                                categoryAdapter.notifyDataSetChanged();
                                progressDailog.dismissDailog();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

    }

    private void InitViews() {
        alertDialogManager = new AlertDialogManager();
        progressDailog  = new ProgressDailog(this);
        showToast = new ShowToast(this);
        subcatrecycler = (RecyclerView)findViewById(R.id.subcatRecycler);
        @SuppressLint("WrongConstant")
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        subcatrecycler.setLayoutManager(manager);
        int spanCount = 2; // 3 columns
        int spacing = 10; // 50px
        boolean includeEdge = false;
        subcatrecycler.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        subcatrecycler.setHasFixedSize(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(SubCategoriesActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
