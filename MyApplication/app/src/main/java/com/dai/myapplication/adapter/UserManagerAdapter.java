package com.dai.myapplication.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dai.myapplication.entity.UserInfo;

import java.util.List;

public class UserManagerAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<UserInfo> userInfoList;
    private DisplayMetrics dm ;

    public UserManagerAdapter(Context context, List<UserInfo> userInfoList) {
        mInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        dm = context.getResources().getDisplayMetrics();
        this.userInfoList = userInfoList;
    }

    @Override
    public int getCount() {
        return userInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userInfoList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
