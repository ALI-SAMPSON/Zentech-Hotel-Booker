package io.zentechhotelbooker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.zip.Inflater;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

import static io.zentechhotelbooker.activities.AddRoomsActivity.Database_Path;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    // context variable and a list of the Rooms
     Context mCtx;
     List<Rooms> roomsList;

    public RecyclerViewAdapter(Context mCtx, List<Rooms> roomsList ){
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // getting the position of each room and its details
        final Rooms rooms = roomsList.get(position);

        /** getting the room details and setting
         * it to the their respective view objects
         * or views
         */
        holder.room_number.setText(" Room Number : " + rooms.getRoom_number());
        holder.room_price.setText(" Price : " + rooms.getPrice());

        //Loading image into Glide using the glide library.
        Glide.with(mCtx).load(rooms.getRoom_image()).into(holder.room_image);

        // on Click listener for the CardView
        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                // Sets a Title and a Message on it
                builder.setTitle(mCtx.getString(R.string.text_delete));
                builder.setMessage(mCtx.getString(R.string.text_delete_body));

                // sets the positive button on the Alert Dialog
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // displays the progress bar
                        holder.progressBar.setVisibility(View.VISIBLE);

                        // getting the ImageUrl from the Rooms class
                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(rooms.getRoom_image());

                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                holder.progressBar.setVisibility(View.GONE);

                                // Getting image upload ID.
                                String ImageUploadID = holder.databaseReference.getKey();

                                // removes values from the database
                                holder.databaseReference.child(ImageUploadID).removeValue();

                                // File deleted successfully message
                                Toast.makeText(mCtx,rooms.getRoom_number() + " deleted Successfully",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // File could not be deleted successfully
                                Toast.makeText(mCtx,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                // sets the positive button on the Alert Dialog
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss the Alert Dialog
                        dialogInterface.dismiss();
                    }
                });

                // Creates a new AlertDialog and displays it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        */

    }

    // class to return the size of the rooms added to the database
    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Creating object of the classes
        CardView cardView;
        ImageView room_image;
        TextView room_number;
        TextView room_price;

        ProgressBar progressBar;

        DatabaseReference databaseReference;

        // constructor
        public ViewHolder(View itemView){
            super(itemView);

            // settings ids to those in the recyclerview_list.xml file
            cardView = itemView.findViewById(R.id.cardView);

            room_image = itemView.findViewById(R.id.room_image);
            room_number = itemView.findViewById(R.id.room_number);
            room_price = itemView.findViewById(R.id.room_price);

            progressBar = itemView.findViewById(R.id.progressBar);

            databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);


        }

    }

}
