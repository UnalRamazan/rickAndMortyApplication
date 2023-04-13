package com.example.rickandmortyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rickandmortyapp.Adapter.CharacterAdapter;
import com.example.rickandmortyapp.Adapter.LocationAdapter;
import com.example.rickandmortyapp.Adapter.LocationListAdapter;
import com.example.rickandmortyapp.Entity.CharacterDetail.Character;
import com.example.rickandmortyapp.Entity.CharacterDetail.ResultForCharacter;
import com.example.rickandmortyapp.Entity.LocationDetail.Location;
import com.example.rickandmortyapp.Entity.LocationDetail.ResultForLocation;
import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.Utility.NetworkChangeListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class LocationListActivity extends AppCompatActivity {

    private String TAG = "LocationListActivity";
    private int page = 1;//başlangınç sayfası
    private LocationListAdapter adapter;
    private ArrayList<ResultForLocation> resultForLocations = new ArrayList<>();

    private ProgressBar progress_bar_for_locations_list;
    private RecyclerView locations_list_recycler_view;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        ImageButton prev_main_image_button = findViewById(R.id.prev_main_image_button);
        progress_bar_for_locations_list = findViewById(R.id.progress_bar_for_locations_list);
        locations_list_recycler_view = findViewById(R.id.locations_list_recycler_view);

        progress_bar_for_locations_list.setVisibility(View.GONE);
        locations_list_recycler_view.setVisibility(View.GONE);

        //Locations recyclerView'ın sonuna gelindiğinde yeni sayfa yüklenmesi için dinleyici ekleniyor
        locations_list_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                //Eğer son item görünürse, yeni sayfa yüklenmesi gerekiyor
                if (lastVisibleItem == totalItemCount - 1) {
                    loadNextPage();
                }
            }
        });

        prev_main_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getLocationFromNetwork();
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

    private void getLocationFromNetwork() {

        progress_bar_for_locations_list.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://rickandmortyapi.com/api/location?page=" + page)
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
                    Location location = new Gson().fromJson(responseBody, Location.class);

                    LocationListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progress_bar_for_locations_list.setVisibility(View.GONE);
                            locations_list_recycler_view.setVisibility(View.VISIBLE);

                            resultForLocations = location.getResults();
                            setAdapterForLocation();
                        }
                    });
                    Log.d(getTAG(), "onResponse");
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterForLocation() {

        if (page == 1) {
            adapter = new LocationListAdapter(resultForLocations);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            locations_list_recycler_view.setLayoutManager(mLayoutManager);
            locations_list_recycler_view.setAdapter(adapter);
        } else {
            adapter.setLocationArrayList(resultForLocations);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isLoading = false;//Sayfanın yüklenip yüklenmediğini kontrol etmek için

    private void loadNextPage() {//Bir sonraki sayfayı yüklemek için
        if (!isLoading) {
            isLoading = true;

            progress_bar_for_locations_list.setVisibility(View.VISIBLE);

            String url = "https://rickandmortyapi.com/api/location?page=" + (page + 1);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Location newLocations = gson.fromJson(response.toString(), Location.class);
                    resultForLocations.addAll(newLocations.getResults());

                    page++; //Bir sonraki sayfayı yükleme
                    isLoading = false; //Yükleme tamamlandı

                    setAdapterForLocation();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Hata oluştuğunda buraya gelecek
                    isLoading = false; //Yükleme tamamlandı
                }
            });

            //Volley isteğini sıraya ekleme
            Volley.newRequestQueue(this).add(request);
            progress_bar_for_locations_list.setVisibility(View.GONE);
        }
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<ResultForLocation> getResultForLocations() {
        return resultForLocations;
    }

    public void setResultForLocations(ArrayList<ResultForLocation> resultForLocations) {
        this.resultForLocations = resultForLocations;
    }
}