package com.example.rickandmortyapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rickandmortyapp.R;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private ArrayList<String> episodesList;

    public EpisodeAdapter(ArrayList<String> episodesList) {
        this.setEpisodesList(episodesList);
    }

    @Override
    public EpisodeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_episode, viewGroup, false);
        EpisodeAdapter.ViewHolder holder = new EpisodeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(EpisodeAdapter.ViewHolder holder, final int position) {

        String episode = episodesList.get(position);

        holder.episode_text_view.setText(episode);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return episodesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView episode_text_view;

        public ViewHolder(View v) {
            super(v);

            episode_text_view = v.findViewById(R.id.episode_text_view);
        }
    }

    public ArrayList<String> getEpisodesList() {
        return episodesList;
    }

    public void setEpisodesList(ArrayList<String> episodesList) {
        this.episodesList = episodesList;
    }
}