package io.zentechhotelbooker.adapters;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.HomeActivity;
import io.zentechhotelbooker.activities.MakePaymentActivity;
import io.zentechhotelbooker.activities.ViewRoomDetailsUserActivity;
import io.zentechhotelbooker.models.Reservations;
import io.zentechhotelbooker.models.Rooms;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder> {

    private Context mCtx;
    private List<Rooms> roomsList;

    // notification variables
    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    public RecyclerViewAdapterUser(Context mCtx, List<Rooms> roomsList) {
        this.mCtx = mCtx;
        this.roomsList = roomsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_room_items,parent,false);

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

        // animation to cardView
        YoYo.with(Techniques.ZoomIn).playOn(holder.room_cardView);

        // sets text to the TextViews
        holder.tv_room_type.setText(rooms.getRoomType());
        holder.tv_room_price.setText(" Price : GH¢ " + rooms.getRoomPrice());
        //holder.room_price.setText(" GH¢ " + rooms.getPrice());

        final String user_image_url = holder.user.getPhotoUrl().toString();

        // using Glide Library to load images
        Glide.with(mCtx).load(rooms.getRoomImage_url()).into(holder.room_image);

        // checks for booked and non-booked rooms
        holder.paymentRef.child(rooms.getRoomNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    // disables the cardView if room is booked
                    //holder.room_cardView.setEnabled(false);
                    holder.room_cardView.setClickable(false);
                    holder.room_cardView.setFocusable(false);

                    // disables the View Details button if room is booked
                    holder.viewDetails.setEnabled(false);
                    holder.viewDetails.setClickable(false);
                    holder.viewDetails.setFocusable(false);

                    // displays a text with caption "Booked" to user
                    holder.tv_room_booked_reserved.setVisibility(View.VISIBLE);

                }
                else{
                    // dismisses the textView
                    holder.tv_room_booked_reserved.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display an error message
                Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        // checks for reserved and non-reserved rooms
        /*holder.reservationRef.child(rooms.getRoomNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    // displays a text with caption "Booked" to user
                    holder.tv_room_booked_reserved.setText(R.string.text_reserved);
                    holder.tv_room_booked_reserved.setVisibility(View.VISIBLE);

                }
                else{
                    // dismisses the textView
                    holder.tv_room_booked_reserved.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display an error message
                Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        */


        // checks if rooms are booked or not when user clicks on the cardView
        holder.room_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //code to the check if room has been booked already
                holder.paymentRef.child(rooms.getRoomNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //gets a snapshot of the data in the database
                        if (dataSnapshot.exists()) {

                            // disables the cardView if room is booked
                            holder.room_cardView.setEnabled(false);
                            holder.room_cardView.setClickable(false);
                            holder.room_cardView.setFocusable(false);

                            // disables the View Details button if room is booked
                            holder.viewDetails.setEnabled(false);
                            holder.viewDetails.setClickable(false);
                            holder.viewDetails.setFocusable(false);

                            holder.tv_room_booked_reserved.setVisibility(View.VISIBLE);

                            // animation to cardView
                            YoYo.with(Techniques.Shake).playOn(holder.room_cardView);

                            // displays a warning message
                            Toast.makeText(mCtx, "Room is already booked", Toast.LENGTH_LONG).show();
                        }
                        else{

                             // Creates an Alert Dialog
                           AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,
                                   android.R.style.Theme_Material_Dialog_Alert);
                           builder.setTitle("Continue...");
                           builder.setMessage("Please select one of the operations to proceed or click cancel to quit");
                           builder.setCancelable(false);

                           // set the Positive button for alert dialog
                           builder.setPositiveButton("BOOK NOW", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {

                                   // Creates new Intent
                                   Intent intentBook = new Intent(mCtx, MakePaymentActivity.class);
                                   intentBook.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   // passing data to the payment activity
                                   intentBook.putExtra("user_name",holder.user.getDisplayName());
                                   intentBook.putExtra("room_number", rooms.getRoomNumber());
                                   intentBook.putExtra("room_type", rooms.getRoomType());
                                   intentBook.putExtra("room_price",  rooms.getRoomPrice());
                                   intentBook.putExtra("breakfast_food", rooms.getBreakfastServed());
                                   intentBook.putExtra("lunch_food", rooms.getLunchServed());
                                   intentBook.putExtra("supper_food",  rooms.getSupperServed());
                                   intentBook.putExtra("room_image_url",rooms.getRoomImage_url());
                                   intentBook.putExtra("user_image", user_image_url);
                                   // starting the activity
                                   mCtx.startActivity(intentBook);
                                   // Add a custom animation to the activity
                                   CustomIntent.customType(mCtx, "left-to-right");

                               }
                           });

                            // set the Positive button for alert dialog
                            builder.setNegativeButton("RESERVE NOW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // dismiss the dialogue interface
                                    dialogInterface.dismiss();
                                    Toast.makeText(mCtx,R.string.room_reserved,Toast.LENGTH_LONG).show();

                                    // getting uid of the user
                                    final String uid  = holder.user.getUid();
                                    final String user_name = holder.user.getDisplayName();
                                    final String mobile_number = holder.users.getMobile_number();
                                    final String room_image_url = rooms.getRoomImage_url();
                                    final String room_number = rooms.getRoomNumber();
                                    final String room_type = rooms.getRoomType();
                                    final String room_price = rooms.getRoomPrice();

                                    holder.reservations.setUid(uid);
                                    holder.reservations.setUser_name(user_name);
                                    holder.reservations.setUser_image_url(user_image_url);
                                    holder.reservations.setMobile_number(mobile_number);
                                    holder.reservations.setRoom_image_url(room_image_url);
                                    holder.reservations.setRoom_number(room_number);
                                    holder.reservations.setRoom_type(room_type);
                                    holder.reservations.setRoom_price(room_price);


                                    holder.reservationRef.child(room_number)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    Reservations reservations = dataSnapshot.getValue(Reservations.class);

                                                    if(dataSnapshot.exists()){

                                                            //Reservations reservations = dataSnapshot.getValue(Reservations.class);

                                                            //if(uid.equals(reservations.getUid()) && room_number.equals(reservations.getRoom_number())){

                                                                Toast.makeText(mCtx, "Oops! ," + user_name +
                                                                                " you have already made reservation for this room "
                                                                        , Toast.LENGTH_LONG).show();
                                                            //}

                                                            /*if (reservations.getRoom_number().equals(room_number)
                                                                    && reservations.getUid().equals(uid)) {

                                                                Toast.makeText(mCtx, "Oops! ," + user_name +
                                                                                " you have already made reservation for this room "
                                                                        , Toast.LENGTH_LONG).show();

                                                            }*/
                                                        }
                                                        else {
                                                            // adds reservation to the system
                                                            holder.reservationRef.child(room_number).
                                                                    setValue(holder.reservations)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                // display a success message
                                                                                Toast.makeText(mCtx, R.string.room_reserved, Toast.LENGTH_LONG).show();

                                                                                /**
                                                                                 * Block of code to send notification to user after reserving a room
                                                                                 */
                                                                                //sends a notification to the user of reserving a room successfully
                                                                                Intent intent = new Intent(mCtx, HomeActivity.class);
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                                PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, 0);

                                                                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, CHANNEL_ID)
                                                                                        .setSmallIcon(R.mipmap.app_icon_round)
                                                                                        .setContentTitle(mCtx.getString(R.string.app_name))
                                                                                        .setContentText(user_name + " you have successfully made reservation for a " + room_type
                                                                                                + " with room number " + room_number + ".")
                                                                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                                                                .bigText(user_name + " you have successfully made reservation for a " + room_type
                                                                                                        + " with room number " + room_number + "."))
                                                                                        // Set the intent that will fire when the user taps the notification
                                                                                        .setWhen(System.currentTimeMillis())
                                                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                                                        .setContentIntent(pendingIntent)
                                                                                        .setAutoCancel(true);

                                                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mCtx);
                                                                                notificationManager.notify(notificationId, mBuilder.build());


                                                                                /**
                                                                                 * Beginning of block of code to send sms to user about reservation made.
                                                                                 */

                                                                                /*
                                                                                 * Block of Code to send sms to user about reservation made

                                                                                String username = "zent-marketing";
                                                                                // password that is to be used along with username

                                                                                String password = "marketin";
                                                                                // Message content that is to be transmitted

                                                                                String message = user_name + " has successfully made reservation for room " + room_number
                                                                                        + " of type " + room_type + ".";


                                                                                String type = "0";

                                                                                String dlr = "1";

                                                                                String destination = "233245134112";

                                                                                // Sender Id to be used for submitting the message
                                                                                String source = "ZENTECH GH";

                                                                                // To what server you need to connect to for submission
                                                                                final String server = "rslr.connectbind.com";

                                                                                // Port that is to be used like 8080 or 8000
                                                                                int port = 2345;

                                                                                try {
                                                                                    // Url that will be called to submit the message
                                                                                    URL sendUrl = new URL("http://" + server + ":" + "/bulksms/bulksms?");
                                                                                    HttpURLConnection httpConnection = (HttpURLConnection) sendUrl
                                                                                            .openConnection();
                                                                                    // This method sets the method type to POST so that
                                                                                    // will be send as a POST request
                                                                                    httpConnection.setRequestMethod("POST");
                                                                                    // This method is set as true wince we intend to send
                                                                                    // input to the server
                                                                                    httpConnection.setDoInput(true);
                                                                                    // This method implies that we intend to receive data from server.
                                                                                    httpConnection.setDoOutput(true);
                                                                                    // Implies do not use cached data
                                                                                    httpConnection.setUseCaches(false);
                                                                                    // Data that will be sent over the stream to the server.
                                                                                    DataOutputStream dataStreamToServer = new DataOutputStream( httpConnection.getOutputStream());
                                                                                    dataStreamToServer.writeBytes("username="
                                                                                            + URLEncoder.encode(username, "UTF-8") + "&password="
                                                                                            + URLEncoder.encode(password, "UTF-8") + "&type="
                                                                                            + URLEncoder.encode(type, "UTF-8") + "&dlr="
                                                                                            + URLEncoder.encode(dlr, "UTF-8") + "&destination="
                                                                                            + URLEncoder.encode(destination, "UTF-8") + "&source="
                                                                                            + URLEncoder.encode(source, "UTF-8") + "&message="
                                                                                            + URLEncoder.encode(message, "UTF-8"));
                                                                                    dataStreamToServer.flush();
                                                                                    dataStreamToServer.close();
                                                                                    // Here take the output value of the server.
                                                                                    BufferedReader dataStreamFromUrl = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()));
                                                                                    String dataFromUrl = "", dataBuffer = "";
                                                                                    // Writing information from the stream to the buffer
                                                                                    while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
                                                                                        dataFromUrl += dataBuffer;
                                                                                    }

                                                                                    dataStreamFromUrl.close();
                                                                                    System.out.println("Response: " + dataFromUrl);
                                                                                }
                                                                                catch (Exception ex) {
                                                                                    // catches any error that occurs and outputs to the user
                                                                                    Toast.makeText(mCtx,ex.getMessage(),Toast.LENGTH_LONG).show();
                                                                                }

                                                                                */

                                                                                /**
                                                                                 * End of block of code to send sms to user about reservation made.
                                                                                 */

                                                                                holder.tv_room_booked_reserved.setText(R.string.text_reserved);
                                                                                holder.tv_room_booked_reserved.setVisibility(View.VISIBLE);

                                                                            } else {
                                                                                Toast.makeText(mCtx, task.getException().getMessage(),
                                                                                        Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });

                                                           }


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }
                            });

                           builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
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

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // display an error message
                        Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


            }
        });



       // OnClickListener for More Details Button
       holder.viewDetails.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               // Creates new Intent and passes the strings to the intent to be opened
               Intent intentDetails = new Intent(mCtx, ViewRoomDetailsUserActivity.class);
               intentDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // passing data to the payment activity
               intentDetails.putExtra("user_name",holder.user.getDisplayName());
               intentDetails.putExtra("room_number", rooms.getRoomNumber());
               intentDetails.putExtra("room_type", rooms.getRoomType());
               intentDetails.putExtra("breakfast","Breakfast : " + rooms.getBreakfastServed());
               intentDetails.putExtra("lunch","Lunch : " + rooms.getLunchServed());
               intentDetails.putExtra("supper","Supper : " + rooms.getSupperServed());
               intentDetails.putExtra("room_price", "GH¢ " + rooms.getRoomPrice());
               intentDetails.putExtra("room_image_url",rooms.getRoomImage_url());
               intentDetails.putExtra("user_image", user_image_url);
               // starting the activity
               mCtx.startActivity(intentDetails);
               // Add a custom animation to the activity
               CustomIntent.customType(mCtx, "fadein-to-fadeout");

           }
       });


    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{

        // Creating objects of the views
        CardView room_cardView;

        ImageView room_image;
        TextView tv_room_booked_reserved;
        TextView tv_room_type;
        TextView tv_room_price;

        Button viewDetails;

        FirebaseAuth mAuth;

        FirebaseUser user;

        Reservations reservations;

        DatabaseReference reservationRef;

        Users users;

        DatabaseReference paymentRef;

        public ViewHolder(View itemView) {
            super(itemView);

            // Assigning ids to the objects
            room_cardView = itemView.findViewById(R.id.room_cardView);
            room_image = itemView.findViewById(R.id.room_image);
            tv_room_booked_reserved = itemView.findViewById(R.id.tv_room_booked);
            tv_room_type = itemView.findViewById(R.id.tv_room_type);
            tv_room_price = itemView.findViewById(R.id.tv_room_price);

            viewDetails = itemView.findViewById(R.id.btn_view);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

            users = new Users();

            reservations = new Reservations();

            reservationRef = FirebaseDatabase.getInstance().getReference("Reservations");

        }

    }

}
