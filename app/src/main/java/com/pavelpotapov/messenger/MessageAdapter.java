package com.pavelpotapov.messenger;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
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

    private List<Message> messages;
    private Activity activity;

    public MessageAdapter(@NonNull Activity context, int resource, List<Message> messages) {
        super(context, resource, messages);
        this.messages = messages;
        this.activity = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        Message message = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            layoutResource = R.layout.my_message_item;
        } else {
            layoutResource = R.layout.you_message_item;
        }

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        boolean isText = message.getImageUrl() == null;
        if (isText) {
            viewHolder.messageName.setText(message.getName());
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageText.setText(message.getText());
        } else {
            viewHolder.messageName.setText(message.getName());
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.messageImage.getContext()).load(message.getImageUrl()).into(viewHolder.messageImage);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int flag;
        Message message = messages.get(position);
        if (message.isMy()) {
            flag = 0;
        } else {
            flag = 1;
        }
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {
        private TextView messageName;
        private TextView messageText;
        private ImageView messageImage;

        public ViewHolder(View view) {
            messageName = view.findViewById(R.id.messageName);
            messageText = view.findViewById(R.id.messageText);
            messageImage = view.findViewById(R.id.messageImage);
        }
    }
}
