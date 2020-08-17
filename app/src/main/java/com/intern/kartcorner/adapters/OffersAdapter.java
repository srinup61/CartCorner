package com.intern.kartcorner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intern.kartcorner.R;
import com.intern.kartcorner.model.CategoriesDAO;
import com.intern.kartcorner.model.OffersDAO;
import com.intern.kartcorner.ui.SubCategoriesActivity;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {

    private List<OffersDAO> offersDAOS;
    private Context mContext;

    public OffersAdapter(Context context, List<OffersDAO> offersDAOS) {
        this.offersDAOS = offersDAOS;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView offerCode,offferName,offerNote,offervalidupto;

        public ViewHolder(View v) {
            super(v);
            offerCode = v.findViewById(R.id.offerCode);
            offferName = v.findViewById(R.id.offerName);
            offerNote = v.findViewById(R.id.offerNote);
            offervalidupto = v.findViewById(R.id.offervalidupto);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        OffersDAO offersDAO = offersDAOS.get(position);
        viewHolder.offerCode.setText("CODE: "+offersDAO.getOfferCode());
        viewHolder.offferName.setText(offersDAO.getOfferName());
        viewHolder.offerNote.setText(offersDAO.getOfferNote());
        viewHolder.offervalidupto.setText("Valid Upto: " +offersDAO.getOfferValidupto());
    }

    @Override
    public int getItemCount() {
        return offersDAOS.size();
    }
}
