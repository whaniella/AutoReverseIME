package com.g.autoreversegit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.g.autoreversegit.R;

import java.util.List;


public class InterfaceNameAdapter extends ArrayAdapter<InterfaceName> {
    private int resourceId;

    public InterfaceNameAdapter(@NonNull Context context, int textViewResourceId,
                        List<InterfaceName> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InterfaceName fruit = getItem(position);  //获取当前项的InterfaceName实例
        //为子项动态加载布局
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView fruitName = (TextView) view.findViewById(R.id.interfaceInfo);
        fruitName.setText(fruit.getName());
        return view;
    }

}
