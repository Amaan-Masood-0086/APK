package com.example.p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberSelectAdapter extends RecyclerView.Adapter<MemberSelectAdapter.ViewHolder> {
    private List<User> users;
    private Set<String> selectedUserIds;

    public MemberSelectAdapter() {
        this.users = new ArrayList<>();
        this.selectedUserIds = new HashSet<>();
    }

    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<String> getSelectedUserIds() {
        return new ArrayList<>(selectedUserIds);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        
        // Set avatar with first letter
        String name = user.getName();
        holder.tvAvatar.setText(name != null && !name.isEmpty() ? 
                String.valueOf(name.charAt(0)).toUpperCase() : "?");
        
        holder.tvMemberName.setText(name);
        holder.tvMemberPhone.setText(user.getMobileNumber());
        
        holder.cbSelect.setChecked(selectedUserIds.contains(user.getUserId()));
        
        View.OnClickListener clickListener = v -> {
            String userId = user.getUserId();
            if (selectedUserIds.contains(userId)) {
                selectedUserIds.remove(userId);
                holder.cbSelect.setChecked(false);
            } else {
                selectedUserIds.add(userId);
                holder.cbSelect.setChecked(true);
            }
        };
        
        holder.itemView.setOnClickListener(clickListener);
        holder.cbSelect.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvMemberName, tvMemberPhone;
        CheckBox cbSelect;

        ViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberPhone = itemView.findViewById(R.id.tvMemberPhone);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }
}
