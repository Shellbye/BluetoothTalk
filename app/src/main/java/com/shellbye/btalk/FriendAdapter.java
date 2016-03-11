package com.shellbye.btalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shellbye on 16/3/7.
 */
public class FriendAdapter extends ArrayAdapter<Friend>{

    private int recourseId;

    public FriendAdapter(Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        recourseId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = getItem(position);

        View view;
        ViewHolder viewHolder;

        // 缓存布局
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(recourseId, null);

            // 缓存控件,防止重复查找
            viewHolder = new ViewHolder();
            viewHolder.addressView = (TextView) view.findViewById(R.id.addressView);
            viewHolder.nameView = (TextView) view.findViewById(R.id.nameView);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.addressView.setText(friend.getAddress());
        viewHolder.nameView.setText(friend.getName());

        return view;
    }

    // 防止每次在getView方法中都调用 View 的 findViewById()方法来获取一次控件的实例
    class ViewHolder {
        TextView addressView;
        TextView nameView;
    }
}
