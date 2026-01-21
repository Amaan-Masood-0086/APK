package com.example.p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.viewmodel.ChatListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<ChatListViewModel.ChatListItem> chatList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(String chatId, boolean isGroup);
    }

    public ChatListAdapter(OnChatClickListener listener) {
        this.chatList = new ArrayList<>();
        this.listener = listener;
    }

    public void setChatList(List<ChatListViewModel.ChatListItem> items) {
        this.chatList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatListViewModel.ChatListItem item = chatList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(item.getChatId(), item.isGroup());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvChatTitle);
            tvSubtitle = itemView.findViewById(R.id.tvChatSubtitle);
        }
    }
}
