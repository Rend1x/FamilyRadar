package com.example.asus.familyradar.model.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.User;
import com.example.asus.familyradar.view.FamilyListActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyListAdapter extends RecyclerView.Adapter<FamilyListAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private DatabaseHelper databaseHelper;
    private Dialog mDialog;

    public FamilyListAdapter(Context context, List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_ithem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        databaseHelper = new DatabaseHelper(context);

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

        holder.deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.delete((String) holder.userEmail.getText());
                userList.remove(position);
                notifyDataSetChanged();
            }
        });

        mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.dialog_contact);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText dialogName = mDialog.findViewById(R.id.dialog_edit_name_friend);
                final TextView dialogEmail = mDialog.findViewById(R.id.dialog_email);
                CircleImageView dialogPhoto = mDialog.findViewById(R.id.dialog_photo_friend);
                Button dialogSave = mDialog.findViewById(R.id.dialog_save_changes_button);
                Button dialogDelete = mDialog.findViewById(R.id.dialog_delete_friend);
                dialogName.setText(userList.get(position).getName());
                dialogEmail.setText(userList.get(position).getEmail());
                Glide.with(dialogPhoto.getContext())
                        .load(userList.get(position).getPhoto())
                        .into(dialogPhoto);
                dialogSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseHelper.updateNameFamily(dialogName.getText().toString(), dialogEmail.getText().toString());
                        notifyDataSetChanged();
                        mDialog.dismiss();
                        Intent accountsIntent = new Intent(context, FamilyListActivity.class);
                        context.startActivity(accountsIntent);

                    }
                });
                dialogDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseHelper.delete((String) dialogEmail.getText());
                        userList.remove(position);
                        notifyDataSetChanged();
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
                Toast.makeText(context,"Test Click " + String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private TextView userName;
        private TextView userEmail;
        private CircleImageView userPhoto;
        private ImageView deleteFriend;
        private CardView cardView;

        private ViewHolder(View item) {
            super(item);

            userName = (TextView) item.findViewById(R.id.itemNameUser);
            userEmail = (TextView) item.findViewById(R.id.itemEmail);
            userPhoto = (CircleImageView) item.findViewById(R.id.itemUserImageView);
            deleteFriend = (ImageView) item.findViewById(R.id.delete);
            cardView = (CardView) item.findViewById(R.id.card_view);
        }

    }
}
