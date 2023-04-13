package com.example.rickandmortyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rickandmortyapp.Adapter.CharacterAdapter;
import com.example.rickandmortyapp.Adapter.EpisodeAdapter;
import com.example.rickandmortyapp.Entity.CharacterDetail.ResultForCharacter;
import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.Utility.CalculateNoOfColumns;
import com.example.rickandmortyapp.Utility.NetworkChangeListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CharacterDetailActivity extends AppCompatActivity {

    private String TAG = "CharacterActivity";
    private String characterId = "";
    private String characterImageUrl = "";
    private ArrayList<String> episodesArraylist = new ArrayList<>();
    private ProgressBar progress_bar_for_actor_detail;
    private ScrollView scroll_view;
    ImageButton download_character;
    private TextView name_of_character_text_view;
    private ImageView image_of_character_image_view;
    private TextView status_of_character_text_view;
    private TextView species_of_character_text_view;
    private TextView gender_of_character_text_view;
    private TextView origin_of_character_text_view;
    private TextView location_of_character_text_view;
    private RecyclerView episodes_list_recycler_view;
    private TextView created_at_of_character_text_view;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        progress_bar_for_actor_detail = findViewById(R.id.progress_bar_for_actor_detail);
        scroll_view = findViewById(R.id.scroll_view);
        ImageButton prev_image_button = findViewById(R.id.prev_image_button);
        download_character = findViewById(R.id.download_character);
        name_of_character_text_view = findViewById(R.id.name_of_character_text_view);
        image_of_character_image_view = findViewById(R.id.image_of_character_image_view);
        status_of_character_text_view = findViewById(R.id.status_of_character_text_view);
        species_of_character_text_view = findViewById(R.id.species_of_character_text_view);
        gender_of_character_text_view = findViewById(R.id.gender_of_character_text_view);
        origin_of_character_text_view = findViewById(R.id.origin_of_character_text_view);
        location_of_character_text_view = findViewById(R.id.location_of_character_text_view);
        episodes_list_recycler_view = findViewById(R.id.episodes_list_recycler_view);
        created_at_of_character_text_view = findViewById(R.id.created_at_of_character_text_view);

        progress_bar_for_actor_detail.setVisibility(View.GONE);
        scroll_view.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setCharacterId(bundle.getString("CharacterId"));
            getCharacterDetailFromNetwork(getCharacterId());
        } else {
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Add Error Image
        }

        prev_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        download_character.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage();
            }
        });
    }

    @Override
    protected void onStart() {

        if (!networkChangeListener.isResult()) {
            finish();
            startActivity(getIntent());
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void getCharacterDetailFromNetwork(String characterId) {
        progress_bar_for_actor_detail.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://rickandmortyapi.com/api/character/" + characterId)
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {

                    final String responseBody = response.body().string();
                    ResultForCharacter character = new Gson().fromJson(responseBody, ResultForCharacter.class);

                    CharacterDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progress_bar_for_actor_detail.setVisibility(View.GONE);
                            scroll_view.setVisibility(View.VISIBLE);
                            download_character.setVisibility(View.GONE);

                            name_of_character_text_view.setText(character.getName());
                            name_of_character_text_view.setSelected(true);

                            Glide.with(CharacterDetailActivity.this)
                                    .load(character.getImage())
                                    .placeholder(R.drawable.baseline_image)
                                    .error(R.drawable.baseline_error)
                                    .centerCrop()
                                    .into(image_of_character_image_view);

                            characterImageUrl = character.getImage();
                            download_character.setVisibility(View.VISIBLE);

                            status_of_character_text_view.setText(character.getStatus());
                            species_of_character_text_view.setText(character.getSpecies());
                            gender_of_character_text_view.setText(character.getGender());
                            origin_of_character_text_view.setText(character.getOrigin().getName());
                            location_of_character_text_view.setText(character.getLocation().getName());

                            int tmpLength;

                            int tmpUrlCharacterLength = "https://rickandmortyapi.com/api/episode/".length();

                            for (String episodes : character.getEpisode()) {
                                tmpLength = episodes.length();

                                episodesArraylist.add(episodes.substring(tmpUrlCharacterLength, tmpLength));
                            }
                            setAdapterForEpisodes(episodesArraylist);

                            String[] parts = character.getCreated().split("T");
                            String str = parts[0] + "T" + " " + parts[1];
                            created_at_of_character_text_view.setText(str);
                        }
                    });
                    Log.d(getTAG(), "onResponse");
                }
            }
        });
    }

    private void setAdapterForEpisodes(ArrayList<String> episodes) {

        EpisodeAdapter adapter = new EpisodeAdapter(episodes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        episodes_list_recycler_view.setLayoutManager(mLayoutManager);
        episodes_list_recycler_view.setAdapter(adapter);
    }

    private void downloadImage() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(characterImageUrl));
        String title = URLUtil.guessFileName(characterImageUrl, null, null);
        request.setTitle(title);
        request.setDescription(getResources().getString(R.string.download));
        String cookie = CookieManager.getInstance().getCookie(characterImageUrl);
        request.addRequestHeader("cookie", cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        DownloadManager downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        Toast.makeText(this, getResources().getString(R.string.start_download), Toast.LENGTH_SHORT).show();
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public ArrayList<String> getEpisodesArraylist() {
        return episodesArraylist;
    }

    public void setEpisodesArraylist(ArrayList<String> episodesArraylist) {
        this.episodesArraylist = episodesArraylist;
    }

    public String getCharacterImageUrl() {
        return characterImageUrl;
    }

    public void setCharacterImageUrl(String characterImageUrl) {
        this.characterImageUrl = characterImageUrl;
    }
}