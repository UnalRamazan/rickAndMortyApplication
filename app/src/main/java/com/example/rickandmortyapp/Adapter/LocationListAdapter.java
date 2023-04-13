package com.example.rickandmortyapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rickandmortyapp.Activities.CharacterDetailActivity;
import com.example.rickandmortyapp.Activities.LocationDetailActivity;
import com.example.rickandmortyapp.Entity.LocationDetail.ResultForLocation;
import com.example.rickandmortyapp.R;

import java.util.ArrayList;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private ArrayList<ResultForLocation> locationArrayList;

    public LocationListAdapter(ArrayList<ResultForLocation> locationArrayList) {
        this.setLocationArrayList(locationArrayList);
    }

    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_location_list, viewGroup, false);
        LocationListAdapter.ViewHolder holder = new LocationListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LocationListAdapter.ViewHolder holder, final int position) {

        ResultForLocation location = locationArrayList.get(position);

        holder.name_of_location_text_view.setText(location.getName());
        holder.type_of_location_text_view.setText(location.getType());
        holder.dimension_of_location_text_view.setText(location.getDimension());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LocationDetailActivity.class);
                String tmpId = String.valueOf(location.getId());
                intent.putExtra("LocationId", tmpId);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name_of_location_text_view;
        private final TextView type_of_location_text_view;
        private final TextView dimension_of_location_text_view;

        public ViewHolder(View v) {
            super(v);

            name_of_location_text_view = v.findViewById(R.id.name_of_location_text_view);
            type_of_location_text_view = v.findViewById(R.id.type_of_location_text_view);
            dimension_of_location_text_view = v.findViewById(R.id.dimension_of_location_text_view);
        }
    }

    public ArrayList<ResultForLocation> getLocationArrayList() {
        return locationArrayList;
    }

    public void setLocationArrayList(ArrayList<ResultForLocation> locationArrayList) {
        this.locationArrayList = locationArrayList;
    }
}