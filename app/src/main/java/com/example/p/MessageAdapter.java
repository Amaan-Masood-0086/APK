package com.example.p;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.Message;
import com.example.p.utils.UserPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private String currentUserId;
    private SimpleDateFormat timeFormat;

    public MessageAdapter(String currentUserId) {
        this.messages = new ArrayList<>();
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        boolean isSent = message.getSenderId().equals(currentUserId);

        holder.tvMessage.setText(message.getContent());
        holder.tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));

        // Alignment based on sender
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageContainer.getLayoutParams();
        if (isSent) {
            params.gravity = Gravity.END;
            holder.messageContainer.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        } else {
            params.gravity = Gravity.START;
            holder.messageContainer.setBackgroundResource(android.R.drawable.dialog_holo_dark_frame);
            if (!message.isGroupMessage()) {
                holder.tvSenderName.setVisibility(View.GONE);
            } else {
                holder.tvSenderName.setVisibility(View.VISIBLE);
                holder.tvSenderName.setText(message.getSenderName());
            }
        }
        holder.messageContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvSenderName;
        LinearLayout messageContainer;

        ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }
    }
}
