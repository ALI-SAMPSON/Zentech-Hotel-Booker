package io.zentechhotelbooker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.MakePaymentActivity;
import io.zentechhotelbooker.models.Rooms;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder> {

    private Context mCtx;
    private List<Rooms> roomsList;

    public RecyclerViewAdapterUser(Context mCtx, List<Rooms> roomsList){
        this.mCtx = mCtx;
        this.roomsList = roomsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        // getting the position of each room and its details
        final Rooms rooms = roomsList.get(position);

        /** getting the room details and setting
         * it to the their respective view objects
         * or views
         */
        /*holder.room_number.setText(" Room Number " + roomsList.get(position).getRoom_number());
        holder.room_price.setText(" Price : GHC " + roomsList.get(position).getPrice());
        Glide.with(context).load(roomsList.get(position).getRoom_image()).into(holder.room_image);
        */
        holder.room_number.setText(" Room Number : " + rooms.getRoom_number());
        holder.room_price.setText(" Price : GHC " + rooms.getPrice());
        final String user_image = holder.user.getPhotoUrl().toString();
        Glide.with(mCtx).load(rooms.getRoom_image()).into(holder.room_image);

        holder.room_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calling the showAlertDialog method
                showAlertDialog();

    }

    // method to call the alert Dialog
    public void showAlertDialog(){
        // Creates an Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Book this Room!");
        builder.setMessage("Are you sure you want to book this room and make payment for it?");
        builder.setCancelable(false);

        // set the Positive button for alert dialog
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Creates new Intent
                Intent intent = new Intent(mCtx, MakePaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // passing data to the payment activity
                intent.putExtra("room_number",rooms.getRoom_number());
                intent.putExtra("room_price",rooms.getPrice());
                intent.putExtra("user_image", user_image);
                // starting the activity
                mCtx.startActivity(intent);
            }
        });

        // set the Positive button for alert dialog
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss the dialogue interface
                dialogInterface.dismiss();
            }
        });

        //
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
        });

    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Creating objects of the views
        CardView room_cardView;

        ImageView room_image;
        TextView room_number;
        TextView room_price;

        FirebaseAuth mAuth;

        FirebaseUser user;

        public ViewHolder(View itemView) {
            super(itemView);

            // Assigning ids to the objects
            room_cardView = itemView.findViewById(R.id.room_cardView);
            room_image = itemView.findViewById(R.id.room_image);
            room_number = itemView.findViewById(R.id.room_number);
            room_price = itemView.findViewById(R.id.room_price);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
        }

    }

}
