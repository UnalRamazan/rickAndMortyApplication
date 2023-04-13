package com.example.rickandmortyapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rickandmortyapp.Entity.LocationDetail.ResultForLocation;
import com.example.rickandmortyapp.PageViewModel.PageViewModel;
import com.example.rickandmortyapp.R;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    protected Context context;
    private ArrayList<ResultForLocation> locationArrayList;
    private int checkedPosition = 0;
    PageViewModel pageViewModel;

    public LocationAdapter(ArrayList<ResultForLocation> locationArrayList, Context context) {
        this.context = context;
        this.locationArrayList = locationArrayList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void SetLocationArrayList(ArrayList<ResultForLocation> locationArrayList) {
        this.locationArrayList = new ArrayList<>();
        this.locationArrayList = locationArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        pageViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.itemView.getContext()).get(PageViewModel.class);
        holder.bind(locationArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout location_linear_layout;
        private final TextView location_name_text_view;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            location_linear_layout = itemView.findViewById(R.id.location_linear_layout);
            location_name_text_view = itemView.findViewById(R.id.location_name_text_view);
        }

        void bind(final ResultForLocation locationData) {

            if (checkedPosition == getAdapterPosition()) {
                location_name_text_view.setTextColor(Color.parseColor("#FFFFFFFF"));
                location_linear_layout.setBackgroundColor(Color.parseColor("#0E171E"));
            } else {
                location_name_text_view.setTextColor(Color.parseColor("#0E171E"));
                location_linear_layout.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }

            location_name_text_view.setText(locationData.getName());

            location_linear_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    location_name_text_view.setTextColor(Color.parseColor("#FFFFFFFF"));
                    location_linear_layout.setBackgroundColor(Color.parseColor("#0E171E"));

                    pageViewModel.setCharacterList(locationData.getResidents());

                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }

    public ArrayList<ResultForLocation> getLocationArrayList() {
        return locationArrayList;
    }

    public void setLocationArrayList(ArrayList<ResultForLocation> locationArrayList) {
        this.locationArrayList = locationArrayList;
    }

    public int getCheckedPosition() {
        return checkedPosition;
    }

    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
    }
}