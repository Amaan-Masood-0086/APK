package com.example.p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.GroupMember;
import com.example.p.database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
    private List<GroupMember> members;
    private Map<String, User> userMap;
    private String currentUserId;
    private boolean isCurrentUserAdmin;
    private OnMemberActionListener listener;

    public interface OnMemberActionListener {
        void onMakeAdmin(GroupMember member);
        void onRemoveAdmin(GroupMember member);
        void onRemoveMember(GroupMember member);
    }

    public GroupMemberAdapter(String currentUserId, OnMemberActionListener listener) {
        this.members = new ArrayList<>();
        this.userMap = new HashMap<>();
        this.currentUserId = currentUserId;
        this.isCurrentUserAdmin = false;
        this.listener = listener;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members != null ? members : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap != null ? userMap : new HashMap<>();
        notifyDataSetChanged();
    }

    public void setCurrentUserAdmin(boolean isAdmin) {
        this.isCurrentUserAdmin = isAdmin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupMember member = members.get(position);
        User user = userMap.get(member.getUserId());
        
        if (user != null) {
            String name = user.getName();
            holder.tvAvatar.setText(name != null && !name.isEmpty() ? 
                    String.valueOf(name.charAt(0)).toUpperCase() : "?");
            holder.tvMemberName.setText(name);
            holder.tvMemberPhone.setText(user.getMobileNumber());
        } else {
            holder.tvAvatar.setText("?");
            holder.tvMemberName.setText(member.getUserId());
            holder.tvMemberPhone.setText("");
        }
        
        // Show admin badge
        holder.tvAdminBadge.setVisibility(member.isAdmin() ? View.VISIBLE : View.GONE);
        
        // Show options button only if current user is admin and not viewing themselves
        boolean canManage = isCurrentUserAdmin && !member.getUserId().equals(currentUserId);
        holder.btnOptions.setVisibility(canManage ? View.VISIBLE : View.GONE);
        
        if (canManage) {
            holder.btnOptions.setOnClickListener(v -> showOptionsMenu(v, member));
        }
    }

    private void showOptionsMenu(View anchor, GroupMember member) {
        PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
        
        if (member.isAdmin()) {
            popup.getMenu().add(0, 1, 0, R.string.remove_admin);
        } else {
            popup.getMenu().add(0, 2, 0, R.string.make_admin);
        }
        popup.getMenu().add(0, 3, 0, R.string.remove_member);
        
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    if (listener != null) listener.onRemoveAdmin(member);
                    return true;
                case 2:
                    if (listener != null) listener.onMakeAdmin(member);
                    return true;
                case 3:
                    if (listener != null) listener.onRemoveMember(member);
                    return true;
            }
            return false;
        });
        
        popup.show();
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvMemberName, tvMemberPhone, tvAdminBadge;
        ImageButton btnOptions;

        ViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberPhone = itemView.findViewById(R.id.tvMemberPhone);
            tvAdminBadge = itemView.findViewById(R.id.tvAdminBadge);
            btnOptions = itemView.findViewById(R.id.btnOptions);
        }
    }
}
