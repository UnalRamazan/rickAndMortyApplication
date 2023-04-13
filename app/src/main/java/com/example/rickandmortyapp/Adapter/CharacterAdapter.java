package com.example.rickandmortyapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rickandmortyapp.Activities.CharacterDetailActivity;
import com.example.rickandmortyapp.Entity.CharacterDetail.ResultForCharacter;
import com.example.rickandmortyapp.R;

import java.util.ArrayList;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

    Integer[] genderIconId = {
            R.drawable.baseline_male,
            R.drawable.baseline_female,
            R.drawable.baseline_blur_circular,
            R.drawable.baseline_question_mark,
    };

    private ArrayList<ResultForCharacter> characterList;

    public CharacterAdapter(ArrayList<ResultForCharacter> characterList) {
        this.setCharacterList(characterList);
    }

    @Override
    public CharacterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_character, viewGroup, false);
        CharacterAdapter.ViewHolder holder = new CharacterAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CharacterAdapter.ViewHolder holder, final int position) {

        ResultForCharacter character = characterList.get(position);

        Glide.with(holder.itemView)
                .load(character.getImage())
                .placeholder(R.drawable.baseline_image)
                .error(R.drawable.baseline_error)
                .centerCrop()
                .into(holder.character_image_view);

        holder.character_name_text_view.setText(character.getName());

        int id;
        switch (character.getGender()) {
            case "Male":
                id = 0;
                break;
            case "Female":
                id = 1;
                break;
            case "Genderless":
                id = 2;
                break;
            default:
                id = 3;
                break;
        }
        Glide.with(holder.itemView)
                .load(genderIconId[id])
                .centerCrop()
                .into(holder.character_gender_image_view);

        String str;
        if (character.getStatus().equals("Alive")) {
            str = "Alive - ";
            holder.character_status_image_view.setImageResource(R.drawable.baseline_green_circle);
        } else if (character.getStatus().equals("Dead")) {
            str = "Dead - ";
            holder.character_status_image_view.setImageResource(R.drawable.baseline_red_circle);
        } else {
            str = "Unknown - ";
            holder.character_status_image_view.setImageResource(R.drawable.baseline_gray_circle);
        }
        holder.character_status_text_view.setText(str);

        str = character.getSpecies();
        holder.character_species_text_view.setText(str);

        str = character.getLocation().getName();
        holder.last_known_location_text_view.setText(str);

        str = character.getOrigin().getName();
        holder.first_seen_in_text_view.setText(str);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), CharacterDetailActivity.class);
                String tmpId = String.valueOf(character.getId());
                intent.putExtra("CharacterId", tmpId);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView character_image_view;
        private final ImageView character_gender_image_view;
        private final ImageView character_status_image_view;
        private final TextView character_name_text_view;
        private final TextView character_status_text_view;
        private final TextView character_species_text_view;
        private final TextView last_known_location_text_view;
        private final TextView first_seen_in_text_view;

        public ViewHolder(View v) {
            super(v);

            character_image_view = v.findViewById(R.id.character_image_view);
            character_gender_image_view = v.findViewById(R.id.character_gender_image_view);
            character_status_image_view = v.findViewById(R.id.character_status_image_view);
            character_name_text_view = v.findViewById(R.id.character_name_text_view);
            character_status_text_view = v.findViewById(R.id.character_status_text_view);
            character_species_text_view = v.findViewById(R.id.character_species_text_view);
            last_known_location_text_view = v.findViewById(R.id.last_known_location_text_view);
            first_seen_in_text_view = v.findViewById(R.id.first_seen_in_text_view);
        }
    }

    public ArrayList<ResultForCharacter> getCharacterList() {
        return characterList;
    }

    public void setCharacterList(ArrayList<ResultForCharacter> characterList) {
        this.characterList = characterList;
    }
}