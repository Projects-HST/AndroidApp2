package com.palprotech.ensyfi.adapter.general;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.palprotech.ensyfi.R;
import com.palprotech.ensyfi.app.AppController;
import com.palprotech.ensyfi.bean.general.viewlist.LeaveStatus;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Admin on 15-07-2017.
 */

public class LeaveStatusListAdapter extends BaseAdapter {

    private static final String TAG = LeaveStatusListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<LeaveStatus> leaveStatus;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();

    public LeaveStatusListAdapter(Context context, ArrayList<LeaveStatus> leaveStatus) {
        this.context = context;
        this.leaveStatus = leaveStatus;

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
            return leaveStatus.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return leaveStatus.get(mValidSearchIndices.get(position));
        } else {
            return leaveStatus.get(position);
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
            convertView = inflater.inflate(R.layout.leave_status_list_item, parent, false);

            holder = new LeaveStatusListAdapter.ViewHolder();
            holder.txtLeaveTitle = (TextView) convertView.findViewById(R.id.txtLeaveTitle);
            holder.txtFromLeaveDate = (TextView) convertView.findViewById(R.id.txtFromLeaveDate);
            holder.txtToLeaveDate = (TextView) convertView.findViewById(R.id.txtToLeaveDate);
            holder.txtFromTime = (TextView) convertView.findViewById(R.id.txtFromTime);
            holder.txtToTime = (TextView) convertView.findViewById(R.id.txtToTime);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            holder.imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);
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

        LeaveStatus leaveStatuses = leaveStatus.get(position);
        if (leaveStatus.get(position).getStatus().contentEquals("Approved")) {
            holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.approve));
            holder.imgStatus.setImageResource(R.drawable.od_approved);
        } else if (leaveStatus.get(position).getStatus().contentEquals("Rejected")) {
            holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.reject));
            holder.imgStatus.setImageResource(R.drawable.od_rejected);
        } else {
            holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.pending));
            holder.imgStatus.setImageResource(R.drawable.od_pending);
        }

        holder.txtLeaveTitle.setText(leaveStatus.get(position).getLeaveTitle());
        holder.txtFromLeaveDate.setText(leaveStatus.get(position).getFromLeaveDate());
        holder.txtToLeaveDate.setText(leaveStatus.get(position).getToLeaveDate());
        holder.txtFromTime.setText(leaveStatus.get(position).getFromTime());
        holder.txtToTime.setText(leaveStatus.get(position).getToTime());
        holder.txtStatus.setText(leaveStatus.get(position).getStatus());
        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < leaveStatus.size(); i++) {
            String leaveStatusTitle = leaveStatus.get(i).getLeaveTitle();
            if ((leaveStatusTitle != null) && !(leaveStatusTitle.isEmpty())) {
                if (leaveStatusTitle.toLowerCase().contains(eventName.toLowerCase())) {
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
        public TextView txtLeaveTitle, txtFromLeaveDate, txtToLeaveDate, txtFromTime, txtToTime, txtStatus;
        public ImageView imgStatus;
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