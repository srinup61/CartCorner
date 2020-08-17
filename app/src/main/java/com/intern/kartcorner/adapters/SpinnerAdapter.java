package com.intern.kartcorner.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.intern.kartcorner.R;
import com.intern.kartcorner.model.Prices;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter {

    private ArrayList<Prices> prices;
    private Context context;
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Prices> prices) {
        super(context, resource, prices);
        this.context = context;
        this.prices = prices;
    }
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_price, parent, false);
        TextView productweight = view.findViewById(R.id.spproductWeight);
        TextView productprice = view.findViewById(R.id.spproductprice);
        TextView productdprice = view.findViewById(R.id.spproductdprice);

        Prices pricesObj = prices.get(position);
        productweight.setText(pricesObj.getProductWeight());
        productprice.setText(pricesObj.getProductPrice());
        productdprice.setText(pricesObj.getProductDPrice());
        String disamount = pricesObj.getProductDPrice();
        if (disamount.equals("0")) {
            productdprice.setVisibility(View.GONE);
            productprice.setPaintFlags(productprice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            productdprice.setVisibility(View.VISIBLE);
            productprice.setPaintFlags(productprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return view;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
