package com.intern.kartcorner.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.OffersAdapter;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.model.Brands;
import com.intern.kartcorner.model.OffersDAO;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import dmax.dialog.SpotsDialog;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class OffersActivity extends AppCompatActivity {
    @BindView(R.id.offersRecyclerview)
    RecyclerView offersRecyclerview;

    private ArrayList<OffersDAO> offersDAOS = new ArrayList<>();
    private OffersAdapter offersAdapter;
    private SpotsDialog spotsDialog;
    private LinearLayoutManager layoutManager;
    private ShowToast showToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CartCorner Offers");

        spotsDialog = new SpotsDialog(this);
        showToast = new ShowToast(this);
        offersRecyclerview = findViewById(R.id.offersRecyclerview);
        layoutManager = new LinearLayoutManager(this);
        offersRecyclerview.setLayoutManager(layoutManager);
        offersRecyclerview.setHasFixedSize(true);

        getOffers();
    }

    private void getOffers() {
        Ion.with(this)
                .load("GET",BASE_URL+"getoffers")
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        spotsDialog.dismiss();
                        showToast.showWarningToast("Check Your Internet");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("offers");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                OffersDAO offersDAO = new OffersDAO();
                                offersDAO.setId(jsonObject1.getString("id"));
                                offersDAO.setOfferId(jsonObject1.getString("offerId"));
                                offersDAO.setOfferName(jsonObject1.getString("offerName"));
                                offersDAO.setOfferCode(jsonObject1.getString("offerCode"));
                                offersDAO.setOfferType(jsonObject1.getString("offerType"));
                                offersDAO.setOfferPrice(jsonObject1.getString("offerPrice"));
                                offersDAO.setOfferPercentage(jsonObject1.getString("offerPercentage"));
                                offersDAO.setOfferNote(jsonObject1.getString("offerNote"));
                                offersDAO.setOfferAmountlimit(jsonObject1.getString("offerAmountlimit"));
                                offersDAO.setOfferValidupto(jsonObject1.getString("offerValidupto"));
                                offersDAO.setOfferInserteddate(jsonObject1.getString("offerInserteddate"));
                                offersDAOS.add(offersDAO);
                            }
                            offersAdapter = new OffersAdapter(OffersActivity.this,offersDAOS);
                            offersRecyclerview.setAdapter(offersAdapter);
                            offersAdapter.notifyDataSetChanged();
                            spotsDialog.dismiss();
                        } catch (JSONException e1) {
                            spotsDialog.dismiss();
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
