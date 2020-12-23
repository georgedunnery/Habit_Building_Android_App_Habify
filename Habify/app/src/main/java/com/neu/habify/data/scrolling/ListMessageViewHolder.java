package com.neu.habify.data.scrolling;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.neu.habify.R;

public class ListMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView message;

    public ListMessageViewHolder(View v) {
        super(v);
        this.message = v.findViewById(R.id.text_list_message);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }
}
