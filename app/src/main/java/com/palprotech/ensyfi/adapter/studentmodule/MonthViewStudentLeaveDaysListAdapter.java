package com.palprotech.ensyfi.adapter.studentmodule;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.palprotech.ensyfi.R;
import com.palprotech.ensyfi.app.AppController;
import com.palprotech.ensyfi.bean.student.viewlist.MonthViewStudentLeaveDays;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Admin on 12-07-2017.
 */

public class MonthViewStudentLeaveDaysListAdapter extends BaseAdapter{

    private static final String TAG = MonthViewListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<MonthViewStudentLeaveDays> monthViewStudentLeaveDays;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();

    public MonthViewStudentLeaveDaysListAdapter(Context context, ArrayList<MonthViewStudentLeaveDays> monthViewStudentLeaveDays) {
        this.context = context;
        this.monthViewStudentLeaveDays = monthViewStudentLeaveDays;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(0)
                .oval(false)
                .build();
        mSearching = false;
    }
    @Override
    public int getCount() {
        if (mSearching) {
            // Log.d("Event List Adapter","Search count"+mValidSearchIndices.size());
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            // Log.d(TAG,"Normal count size");
            return monthViewStudentLeaveDays.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return monthViewStudentLeaveDays.get(mValidSearchIndices.get(position));
        } else {
            return monthViewStudentLeaveDays.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.month_view_student_leave_days_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtEnrollId = (TextView) convertView.findViewById(R.id.txtEnrollId);
            holder.txtStudentName = (TextView) convertView.findViewById(R.id.txtStudentName);
            holder.txtLeaveDays = (TextView) convertView.findViewById(R.id.txtLeaveDays);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            // Log.d("Event List Adapter","actual position"+ position);
            position = mValidSearchIndices.get(position);
            //Log.d("Event List Adapter", "position is"+ position);

        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        MonthViewStudentLeaveDays monthViewStudentLeaveDay = monthViewStudentLeaveDays.get(position);

        holder.txtEnrollId.setText(monthViewStudentLeaveDays.get(position).getEnrollId());
        holder.txtStudentName.setText(monthViewStudentLeaveDays.get(position).getName());
        holder.txtLeaveDays.setText(monthViewStudentLeaveDays.get(position).getAbsDate());
        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < monthViewStudentLeaveDays.size(); i++) {
            String monthViewTitle = monthViewStudentLeaveDays.get(i).getName();
            if ((monthViewTitle != null) && !(monthViewTitle.isEmpty())) {
                if (monthViewTitle.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }

            }

        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
        //notifyDataSetChanged();
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
        // notifyDataSetChanged();
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtEnrollId, txtStudentName, txtLeaveDays;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}