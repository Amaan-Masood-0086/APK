package com.example.p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.User;

import java.util.ArrayList;
import java.util.List;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.ViewHolder> {
    private List<User> users;
    private OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAddClick(String userId);
    }

    public AddMemberAdapter(OnAddClickListener listener) {
        this.users = new ArrayList<>();
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        
        String name = user.getName();
        holder.tvAvatar.setText(name != null && !name.isEmpty() ? 
                String.valueOf(name.charAt(0)).toUpperCase() : "?");
        holder.tvMemberName.setText(name);
        holder.tvMemberPhone.setText(user.getMobileNumber());
        
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick(user.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvMemberName, tvMemberPhone;
        Button btnAdd;

        ViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberPhone = itemView.findViewById(R.id.tvMemberPhone);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
