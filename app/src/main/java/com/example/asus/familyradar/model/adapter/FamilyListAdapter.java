package com.example.asus.familyradar.model.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.User;
import com.example.asus.familyradar.view.FamilyListActivity;
import com.example.asus.familyradar.view.fragment.FamilyListFragment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyListAdapter extends RecyclerView.Adapter<FamilyListAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public FamilyListAdapter(Context context, List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_ithem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.userName.setText(userList.get(position).getName());
        holder.userEmail.setText(userList.get(position).getEmail());

        if (userList.get(position).getPhoto() == null) {
            holder.userPhoto.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(holder.userPhoto.getContext())
                    .load(userList.get(position).getPhoto())
                    .into(holder.userPhoto);
        }

        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private TextView userName;
        private TextView userEmail;
        private CircleImageView userPhoto;
        private CheckBox checkBox;

        private ViewHolder(View item) {
            super(item);

            userName = (TextView) item.findViewById(R.id.itemNameUser);
            userEmail = (TextView) item.findViewById(R.id.itemEmail);
            userPhoto = (CircleImageView) item.findViewById(R.id.itemUserImageView);
            checkBox = (CheckBox) item.findViewById(R.id.checkBox);

        }

    }
}
