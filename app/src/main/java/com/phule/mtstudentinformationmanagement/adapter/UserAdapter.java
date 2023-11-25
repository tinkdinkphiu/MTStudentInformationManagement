package com.phule.mtstudentinformationmanagement.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.User;
import com.phule.mtstudentinformationmanagement.helper.RetrofitClient;
import com.phule.mtstudentinformationmanagement.helper.UserApiService;
import com.phule.mtstudentinformationmanagement.ui.activity.DetailsStudentActivity;
import com.phule.mtstudentinformationmanagement.ui.activity.DetailsUserActivity;
import com.phule.mtstudentinformationmanagement.ui.activity.EditUserActivity;
import com.phule.mtstudentinformationmanagement.ui.fragment.UserManagerFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                popupMenu.getMenuInflater().inflate(R.menu.menu_item_student_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.menu_details) {
                            // See details action
                            Intent intent = new Intent(view.getContext(), DetailsUserActivity.class);
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("name", user.getName());
                            intent.putExtra("age", user.getAge());
                            intent.putExtra("phone", user.getPhone());
                            intent.putExtra("role", user.getRole());
                            intent.putExtra("status", user.getStatus());
                            view.getContext().startActivity(intent);
                        }
                        else if (menuItem.getItemId() == R.id.menu_edit) {
                            if(userManagerFragment.hasAuthority()) {
                                // Edit action
                                Intent intent = new Intent(view.getContext(), EditUserActivity.class);
                                intent.putExtra("email", user.getEmail());
                                intent.putExtra("name", user.getName());
                                intent.putExtra("age", user.getAge());
                                intent.putExtra("phone", user.getPhone());
                                intent.putExtra("status", user.getStatus());
                                intent.putExtra("role", user.getRole());
                                // ReloadAfterEditUser(1) - Pass intent to UserListFragment
                                userManagerFragment.receiveFromAdapter(intent);
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
                                                // Remove user
                                                UserApiService service = RetrofitClient.getService();
                                                Call<UserApiService.ApiResponse> call = service.deleteUser(new UserApiService.UserRespone(user.getEmail()));

                                                call.enqueue(new Callback<UserApiService.ApiResponse>() {
                                                    @Override
                                                    public void onResponse(Call<UserApiService.ApiResponse> call, Response<UserApiService.ApiResponse> response) {
                                                        if(response.isSuccessful()) {
                                                            // Handle response
                                                            UserApiService.ApiResponse apiResponse = response.body();
                                                            Log.d("Response", "Success: " + apiResponse.success + ", Message: " + apiResponse.message);
                                                        } else {
                                                            // Handle request errors depending on status code
                                                            Log.d("Response", "Error: " + response.code() + response.message());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<UserApiService.ApiResponse> call, Throwable t) {
                                                        Log.d("Response", "Failure: " + t.getMessage());
                                                    }
                                                });

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