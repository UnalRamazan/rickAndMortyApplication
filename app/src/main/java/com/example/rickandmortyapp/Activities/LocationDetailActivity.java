package com.example.rickandmortyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.rickandmortyapp.Adapter.CharacterAdapter;
import com.example.rickandmortyapp.Entity.CharacterDetail.ResultForCharacter;
import com.example.rickandmortyapp.Entity.LocationDetail.ResultForLocation;
import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.Utility.NetworkChangeListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationDetailActivity extends AppCompatActivity {

    private String TAG = "LocationDetailActivity";
    private String locationId = "";
    private ProgressBar progress_bar_for_location_detail;
    private ProgressBar progress_bar_for_characters_list;
    private LinearLayout location_detail_linear_layout;
    private TextView name_of_location_text_view;
    private TextView type_of_location_text_view;
    private TextView dimension_of_location_text_view;
    private TextView created_of_location_text_view;
    private RecyclerView character_list_in_location_detail_recycler_view;
    private LinearLayout character_list_empty_in_location_detail_linear_layout;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        progress_bar_for_location_detail = findViewById(R.id.progress_bar_for_location_detail);
        progress_bar_for_characters_list = findViewById(R.id.progress_bar_for_characters_list);
        location_detail_linear_layout = findViewById(R.id.location_detail_linear_layout);
        ImageButton prev_image_button = findViewById(R.id.prev_image_button);
        name_of_location_text_view = findViewById(R.id.name_of_location_text_view);
        type_of_location_text_view = findViewById(R.id.type_of_location_text_view);
        dimension_of_location_text_view = findViewById(R.id.dimension_of_location_text_view);
        created_of_location_text_view = findViewById(R.id.created_of_location_text_view);
        character_list_in_location_detail_recycler_view = findViewById(R.id.character_list_in_location_detail_recycler_view);
        character_list_empty_in_location_detail_linear_layout = findViewById(R.id.character_list_empty_in_location_detail_linear_layout);

        progress_bar_for_location_detail.setVisibility(View.GONE);
        location_detail_linear_layout.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setLocationId(bundle.getString("LocationId"));
            getLocationDetailFromNetwork(getLocationId());
        } else {
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Add Error Image
        }

        prev_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private void getLocationDetailFromNetwork(String locationId) {
        progress_bar_for_location_detail.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://rickandmortyapi.com/api/location/" + locationId)
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
                    ResultForLocation location = new Gson().fromJson(responseBody, ResultForLocation.class);

                    LocationDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progress_bar_for_location_detail.setVisibility(View.GONE);
                            location_detail_linear_layout.setVisibility(View.VISIBLE);
                            progress_bar_for_characters_list.setVisibility(View.GONE);
                            character_list_in_location_detail_recycler_view.setVisibility(View.GONE);
                            character_list_empty_in_location_detail_linear_layout.setVisibility(View.GONE);

                            name_of_location_text_view.setText(location.getName());
                            name_of_location_text_view.setSelected(true);

                            type_of_location_text_view.setText(location.getType());
                            dimension_of_location_text_view.setText(location.getDimension());

                            String[] parts = location.getCreated().split("T");
                            String str = parts[0] + "T" + " " + parts[1];
                            created_of_location_text_view.setText(str);

                            if (!location.getResidents().isEmpty()) {
                                int tmpLength;
                                String tmpCharactersList = "https://rickandmortyapi.com/api/character/";
                                int tmpUrlCharacterLength = tmpCharactersList.length();

                                for (String character : location.getResidents()) {
                                    tmpLength = character.length();

                                    tmpCharactersList += character.substring(tmpUrlCharacterLength, tmpLength) + ", ";
                                }
                                getCharacterFromNetwork(tmpCharactersList);
                            } else {
                                character_list_empty_in_location_detail_linear_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    Log.d(getTAG(), "onResponse");
                }
            }
        });
    }

    private void getCharacterFromNetwork(String idListOfCharacters) {
        progress_bar_for_characters_list.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(idListOfCharacters)
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {

                    final String responseBody = response.body().string();
                    ArrayList<ResultForCharacter> mainCharactersList = new ArrayList<>();

                    Type characterListType = new TypeToken<ArrayList<ResultForCharacter>>() {
                    }.getType();
                    mainCharactersList = new Gson().fromJson(responseBody, characterListType);

                    ArrayList<ResultForCharacter> finalMainCharactersList = mainCharactersList;
                    LocationDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress_bar_for_characters_list.setVisibility(View.GONE);
                            character_list_in_location_detail_recycler_view.setVisibility(View.VISIBLE);

                            setAdapterForCharacters(finalMainCharactersList);
                        }
                    });
                    Log.d(getTAG(), "onResponse");
                }
            }
        });
    }

    private void setAdapterForCharacters(ArrayList<ResultForCharacter> resultForCharacters) {

        CharacterAdapter adapter = new CharacterAdapter(resultForCharacters);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        character_list_in_location_detail_recycler_view.setLayoutManager(mLayoutManager);
        character_list_in_location_detail_recycler_view.setAdapter(adapter);
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}