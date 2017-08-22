package com.example.twilightlemon.lemonapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RobotAdapter extends BaseAdapter {
    View[] itemViews;
    private static LayoutInflater inflater=null;
    public RobotAdapter(Activity a, ArrayList<String> RTEXT, ArrayList<String> UTEXT,
                        Bitmap UTX, String UNAME){
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemViews = new View[RTEXT.size()];

        for (int i=0; i<itemViews.length; ++i){
            itemViews[i] = makeItemView(RTEXT.get(i), UTEXT.get(i),
                    UTX,UNAME);
        }
    }

    public int getCount()  {
        return itemViews.length;
    }

    public View getItem(int position)  {
        return itemViews[position];
    }

    public long getItemId(int position) {
        return position;
    }

    private View makeItemView(String strTitle, String strText, Bitmap UTX,String UNAME) {
        View itemView = inflater.inflate(R.layout.robotitems, null);
        TextView name= (TextView)itemView.findViewById(R.id.UNAME);
        name.setText(UNAME);
        TextView title = (TextView)itemView.findViewById(R.id.RTEXT);
        title.setText(strTitle);
        TextView text = (TextView)itemView.findViewById(R.id.UTEXT);
        text.setText(strText);
        ImageView image = (ImageView)itemView.findViewById(R.id.UTX);
        image.setImageBitmap(UTX);

        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return itemViews[position];
        return convertView;
    }
}
