package com.intern.kartcorner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intern.kartcorner.R;
import com.intern.kartcorner.model.SubCategoriesDAO;
import com.intern.kartcorner.ui.AllItemsActivity;

import java.util.ArrayList;

public class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.ViewHolder> {

    private ArrayList<SubCategoriesDAO> mValues;
    private Context mContext;

    public SubCatAdapter(Context context, ArrayList<SubCategoriesDAO> values) {
        mValues = values;
        mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subcatname;
        private ImageView subcatimage;
        private CardView cardView;

        public ViewHolder(View v) {
            super(v);
            subcatname = (TextView) v.findViewById(R.id.single_subcat_name);
            subcatimage = (ImageView) v.findViewById(R.id.single_subcat_image);
            cardView = v.findViewById(R.id.cardView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rowitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        SubCategoriesDAO subCategoriesDAO = mValues.get(position);
        viewHolder.subcatname.setText(subCategoriesDAO.getSubcategoryname());
        String url = subCategoriesDAO.getSubcategoryimage();
        Glide.with(mContext).load(url).into(viewHolder.subcatimage);
        viewHolder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AllItemsActivity.class);
            intent.putExtra("catid",subCategoriesDAO.getCategoryid());
            intent.putExtra("subcatid",subCategoriesDAO.getSubcategoryid());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
