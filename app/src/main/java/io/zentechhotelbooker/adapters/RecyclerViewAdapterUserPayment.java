package io.zentechhotelbooker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Payments;

public class RecyclerViewAdapterUserPayment extends RecyclerView.Adapter<RecyclerViewAdapterUserPayment.ViewHolder> {

    Context mCtx;
    List<Payments> user_payment_list;

    public RecyclerViewAdapterUserPayment(Context mCtx, List<Payments> user_payment_list){
        this.mCtx = mCtx;
        this.user_payment_list = user_payment_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_user_payment_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Payments payments = user_payment_list.get(position);

        holder.user_name.setText(" Username : " + payments.getUser_name());
        holder.room_number.setText(" Room Number : "  + payments.getRoom_number());
        holder.room_price.setText(" Price : GHC " + payments.getPrice());
        holder.momo_number.setText(" M. Number : " + payments.getMobile_number());
        Glide.with(mCtx).load(payments.getImageUrl()).into(holder.room_image);

    }

    @Override
    public int getItemCount() {
        return user_payment_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView room_image;
        TextView user_name;
        TextView room_number;
        TextView room_price;
        TextView momo_number;

        public ViewHolder(View itemView) {
            super(itemView);

            room_image = itemView.findViewById(R.id.room_image);
            user_name = itemView.findViewById(R.id.user_name);
            room_number = itemView.findViewById(R.id.room_number);
            room_price = itemView.findViewById(R.id.room_price);
            momo_number = itemView.findViewById(R.id.mobile_number);

        }
    }

}
