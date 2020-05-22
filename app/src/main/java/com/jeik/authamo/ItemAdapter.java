package com.jeik.authamo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ItemAdapter extends ArrayAdapter<File> {
    public ItemAdapter(@NonNull Context context, int resource, @NonNull List<File> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
        }

        File item = getItem(position);

        String status= getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("status","");

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        if (item.getName().equals("AuthKey") && status.equals("main")){
            tvTitle.setText("Main");
        }
        else if (item.getName().equals("AuthKeyMain") && status.equals("alt")){
            tvTitle.setText("Main");
        }
        else if (item.getName().equals("AuthKeyAlt") && status.equals("main")){
            tvTitle.setText("Alt");
        }
        else if (item.getName().equals("AuthKey") && status.equals("alt")){
            tvTitle.setText("Alt");
        }
        String yoh = item.getName();
        if(yoh.equals("AuthKey")){
            tvTitle.setTextColor(Color.BLUE);
        }
        else tvTitle.setTextColor(Color.BLACK);


        convertView.setEnabled(false);
        return convertView;
    }

}
