package com.intern.kartcorner.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.CheckoutAdapter;
import com.intern.kartcorner.model.OrdersCommonClass;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewitemsActivity extends AppCompatActivity {
    @BindView(R.id.viewitemsRecycler)
    RecyclerView itemsREcycler;
    public ArrayList<OrdersCommonClass> commonClasses = new ArrayList<>();
    private String products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewitems);
        ButterKnife.bind(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        itemsREcycler.setLayoutManager(llm);
        itemsREcycler.setHasFixedSize(true);
        products = getIntent().getStringExtra("items");
        Gson gson = new Gson();
        commonClasses=gson.fromJson(products, new TypeToken<List<OrdersCommonClass>>() {}.getType());
        CheckoutAdapter listCheckoutAdapter = new CheckoutAdapter(ViewitemsActivity.this,commonClasses, ViewitemsActivity.this,2);
        itemsREcycler.setAdapter(listCheckoutAdapter);
        listCheckoutAdapter.notifyDataSetChanged();
    }
}
