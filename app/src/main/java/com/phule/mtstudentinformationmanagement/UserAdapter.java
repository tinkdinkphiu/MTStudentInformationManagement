package com.phule.mtstudentinformationmanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private UserManagerFragment userManagerFragment;
    private List<User> userList;

    public UserAdapter(List<User> userList, UserManagerFragment userManagerFragment) {
        this.userList = userList;
        this.userManagerFragment = userManagerFragment;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.roleTextView.setText(user.getRole());
        holder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.tvOption);
                popupMenu.getMenuInflater().inflate(R.menu.menu_item_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.menu_edit) {
                            if(userManagerFragment.hasAuthority()) {
                                // Edit action
                                Intent intent = new Intent(view.getContext(), EditUserActivity.class);
                                intent.putExtra("email", user.getEmail());
                                intent.putExtra("name", user.getName());
                                intent.putExtra("age", user.getAge());
                                intent.putExtra("phone", user.getPhone());
                                intent.putExtra("status", user.getStatus());
                                intent.putExtra("role", user.getRole());
                                // @TODO: continue update action
                            }
                            else {
                                Toast.makeText(view.getContext(), "You don't have the authority to do this action", Toast.LENGTH_SHORT).show();
                            }
                        } else if (menuItem.getItemId() == R.id.menu_remove) {
                            if(userManagerFragment.hasAuthority()) {
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle(view.getContext().getString(R.string.confirm_removal_title))
                                        .setMessage(view.getContext().getString(R.string.confirm_removal_msg_user))
                                        .setIcon(view.getContext().getDrawable(R.drawable.baseline_warning_24))
                                        .setPositiveButton(view.getContext().getString(R.string.confirm_removal_accept), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Remove user from Firestore

                                            }
                                        })
                                        .setNegativeButton(view.getContext().getString(R.string.confirm_removal_deny), null)
                                        .show();
                            }
                            else {
                                Toast.makeText(view.getContext(), "You don't have the authority to do this action", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, roleTextView, tvOption;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_user_name);
            roleTextView = itemView.findViewById(R.id.item_user_role);
            tvOption = itemView.findViewById(R.id.item_tv_option);
        }
    }
}