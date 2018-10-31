package io.zentechhotelbooker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

public class RecyclerViewAdapterViewRoomDetails extends RecyclerView.Adapter<RecyclerViewAdapterViewRoomDetails.ViewHolder> {

    private Context mCtx;
    private List<Rooms> roomsList;

    public RecyclerViewAdapterViewRoomDetails(Context mCtx, List<Rooms> roomsList){
        this.mCtx = mCtx;
        this.roomsList = roomsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_view_room_details,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{



        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
