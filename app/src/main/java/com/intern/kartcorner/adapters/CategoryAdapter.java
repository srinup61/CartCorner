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
import com.intern.kartcorner.model.CategoriesDAO;
import com.intern.kartcorner.ui.SubCategoriesActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoriesDAO> mValues;
    private Context mContext;

    public CategoryAdapter(Context context, List<CategoriesDAO> values) {
        mValues = values;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView catgeoryname;
        private ImageView categoryimage;
        private CardView cardView;

        public ViewHolder(View v) {
            super(v);
            catgeoryname = (TextView) v.findViewById(R.id.single_subcat_name);
            categoryimage = (ImageView) v.findViewById(R.id.single_subcat_image);
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
        CategoriesDAO categoriesDAO = mValues.get(position);
        viewHolder.catgeoryname.setText(categoriesDAO.getCategoryname());
        String url = categoriesDAO.getCategoryimage();
        Glide.with(mContext).load(url).into(viewHolder.categoryimage);

        viewHolder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, SubCategoriesActivity.class);
            intent.putExtra("catid",categoriesDAO.getCategoryId());
            intent.putExtra("catname",categoriesDAO.getCategoryname());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
