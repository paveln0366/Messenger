package com.pavelpotapov.messenger;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(@NonNull Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_item,
                    parent, false);
        }
        ImageView messageImage = convertView.findViewById(R.id.messageImage);
        TextView messageText = convertView.findViewById(R.id.messageText);
        TextView messageName = convertView.findViewById(R.id.messageName);
        Message message = getItem(position);
        boolean isText = message.getImageUrl() == null;
        if (isText) {
            messageText.setVisibility(View.VISIBLE);
            messageImage.setVisibility(View.GONE);
            messageText.setText(message.getText());
        } else {
            messageText.setVisibility(View.GONE);
            messageImage.setVisibility(View.VISIBLE);
            Glide.with(messageImage.getContext()).load(message.imageUrl).into(messageImage);
        }
        messageName.setText(message.getName());
        return convertView;
    }
}
