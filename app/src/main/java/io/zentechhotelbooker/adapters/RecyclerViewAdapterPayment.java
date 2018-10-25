package io.zentechhotelbooker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Payments;

public class RecyclerViewAdapterPayment extends RecyclerView.Adapter<RecyclerViewAdapterPayment.ViewHolder> {

    Context mCtx;
    List<Payments> paymentsList;

    public RecyclerViewAdapterPayment(Context mCtx, List<Payments> paymentsList){
        this.mCtx = mCtx;
        this.paymentsList = paymentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_payment_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Payments payments = paymentsList.get(position);

        // animation to cardView
        YoYo.with(Techniques.ZoomIn).playOn(holder.cardView);

        holder.user_name.setText(" Username : " + payments.getUser_name());
        holder.room_number.setText(" Room Type : "  + payments.getRoom_type());
        holder.room_price.setText(" Price : GHC " + payments.getPrice());
        holder.mobile_number.setText(" M. Number : " + payments.getMobile_number());
        Glide.with(mCtx).load(payments.getImageUrl()).into(holder.room_image);

    }

    @Override
    public int getItemCount() {
        return paymentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Creating object of the classes
        RecyclerView recyclerView;
        CardView cardView;
        ImageView room_image;
        TextView room_number;
        TextView user_name;
        TextView room_price;
        TextView mobile_number;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.payment_cardView);
            room_image = itemView.findViewById(R.id.room_image);
            user_name = itemView.findViewById(R.id.user_name);
            room_number = itemView.findViewById(R.id.room_number);
            room_price = itemView.findViewById(R.id.room_price);
            mobile_number = itemView.findViewById(R.id.mobile_number);

        }
    }


}
