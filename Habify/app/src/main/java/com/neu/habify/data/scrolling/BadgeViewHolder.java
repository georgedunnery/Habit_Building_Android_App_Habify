package com.neu.habify.data.scrolling;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.neu.habify.R;

public class BadgeViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView value;
    private ImageView icon;

    public BadgeViewHolder(View v) {
        super(v);
        this.name = v.findViewById(R.id.text_badge_name);
        this.value = v.findViewById(R.id.text_badge_point_value);
        this.icon = v.findViewById(R.id.image_badge_icon);
    }

    public void setName(String name) {
        if (name.contains("streak"))
            this.name.setText("Streak Award");
        else
            this.name.setText("Incremental Improvement Award");
    }

    public void setValue(Integer value) {
        this.value.setText("Points got: " + Integer.toString(value));
    }

    public void setIcon(String iconPath) {
        Context context = this.icon.getContext();
        int id = context.getResources().getIdentifier(iconPath, "drawable", context.getPackageName());
        this.icon.setImageResource(id);
    }
}
