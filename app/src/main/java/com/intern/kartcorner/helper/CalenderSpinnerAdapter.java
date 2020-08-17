package com.intern.kartcorner.helper;

/**
 * Created by Prabhu on 11-12-2017.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalenderSpinnerAdapter extends BaseAdapter {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("d MMM yyyy");

    private LayoutInflater mInflater;
    private Calendar mCalendar;
    private int mDayCount;
    private int mLastRequestedDay = -1;

    public CalenderSpinnerAdapter(Context context, int dayCount) {
        mInflater = LayoutInflater.from(context);
        mDayCount = dayCount;
        mCalendar = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return mDayCount;
    }

    @Override
    public Calendar getItem(int position) {
        mCalendar.add(Calendar.DAY_OF_YEAR, position - mLastRequestedDay);
        mLastRequestedDay = position;
        return mCalendar;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        Calendar item = getItem(position);
        ((TextView) convertView).setText(mDateFormat.format(item.getTimeInMillis()));


        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
