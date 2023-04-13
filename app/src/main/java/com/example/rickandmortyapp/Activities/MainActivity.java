package com.example.rickandmortyapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rickandmortyapp.Adapter.CharacterAdapter;
import com.example.rickandmortyapp.Adapter.LocationAdapter;
import com.example.rickandmortyapp.Entity.CharacterDetail.ResultForCharacter;
import com.example.rickandmortyapp.Entity.LocationDetail.Location;
import com.example.rickandmortyapp.Entity.LocationDetail.ResultForLocation;
import com.example.rickandmortyapp.PageViewModel.PageViewModel;
import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.Utility.NetworkChangeListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private int page = 1;//lokasyonlar için başlangınç sayfası
    private LocationAdapter locationAdapter;
    private ArrayList<ResultForLocation> locationArrayList = new ArrayList<>();//Lokasyonların listesi
    private ProgressBar progress_bar_for_locations;
    private ProgressBar progress_bar_for_characters;
    private RecyclerView locations_recycler_view;
    private RecyclerView characters_recycler_view;

    //Her lokasyon değişimde lokasyonlarda bulunan karakterlerin id listesi hemen "MainActivity" ye transfer ediliyor.
    //MainActivity de bulunan characters_recycler_view güncelleniyor.
    PageViewModel pageViewModel;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);

        ImageButton location_list_image_button = findViewById(R.id.location_list_image_button);
        ImageButton character_list_image_button = findViewById(R.id.character_list_image_button);
        progress_bar_for_locations = findViewById(R.id.progress_bar_for_locations);
        progress_bar_for_characters = findViewById(R.id.progress_bar_for_characters);
        locations_recycler_view = findViewById(R.id.locations_recycler_view);
        characters_recycler_view = findViewById(R.id.characters_recycler_view);
        LinearLayout character_list_empty_linear_layout = findViewById(R.id.character_list_empty_linear_layout);

        progress_bar_for_locations.setVisibility(View.GONE);
        progress_bar_for_characters.setVisibility(View.GONE);
        locations_recycler_view.setVisibility(View.GONE);
        characters_recycler_view.setVisibility(View.GONE);
        character_list_empty_linear_layout.setVisibility(View.GONE);

        //Kararter listesindeki değişiklikler dinleniyor, değişiklik olduğunda bu metot çalışacak
        pageViewModel.getCharacterList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> characterIdList) {

                if (!characterIdList.isEmpty()) {
                    //Eğer karakter listesi boş değilse, karakterleri ağdan iste
                    character_list_empty_linear_layout.setVisibility(View.GONE);
                    characters_recycler_view.setVisibility(View.GONE);//Her yeni lokasyon tıklanmasında mevcut verilerin gözükmemesi için
                    getCharacterFromNetwork(characterIdList);
                } else {
                    //Eğer karakter listesi boş ise hata mesajı göster
                    progress_bar_for_characters.setVisibility(View.GONE);
                    characters_recycler_view.setVisibility(View.GONE);
                    character_list_empty_linear_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        //Locations recyclerView'ın sonuna gelindiğinde yeni sayfa yüklenmesi için dinleyici ekleniyor
        locations_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        location_list_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocationListActivity.class);
                startActivity(intent);
            }
        });

        character_list_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CharacterListActivity.class);
                startActivity(intent);
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

    private void getLocationFromNetwork() {

        progress_bar_for_locations.setVisibility(View.VISIBLE);

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

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progress_bar_for_locations.setVisibility(View.GONE);
                            locations_recycler_view.setVisibility(View.VISIBLE);

                            locationArrayList = location.getResults();
                            setAdapterForLocations();

                            //Uygulama açıldığında ilk lokasyonun karakterlerini göstermek için
                            getCharacterFromNetwork(location.getResults().get(0).getResidents());
                        }
                    });
                    Log.d(getTAG(), "onResponseForLocation");
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterForLocations() {

        if (page == 1) {

            ArrayList<ResultForLocation> tmpLocationArrayList = new ArrayList<>();

            locationAdapter = new LocationAdapter(tmpLocationArrayList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            locations_recycler_view.setLayoutManager(mLayoutManager);
            locations_recycler_view.setAdapter(locationAdapter);

            locationAdapter.SetLocationArrayList(locationArrayList);
        } else {
            locationAdapter.SetLocationArrayList(locationArrayList);
            locationAdapter.notifyDataSetChanged();
        }
    }

    private boolean isLoading = false;//Sayfanın yüklenip yüklenmediğini kontrol etmek için
    private void loadNextPage() {//Bir sonraki lokasyon sayfasını yüklemek için
        if (!isLoading) {
            isLoading = true;

            progress_bar_for_locations.setVisibility(View.VISIBLE);

            String url = "https://rickandmortyapi.com/api/location?page=" + (page + 1);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Location newLocations = gson.fromJson(response.toString(), Location.class);
                    locationArrayList.addAll(newLocations.getResults());

                    page++; //Bir sonraki sayfayı yükleme
                    isLoading = false; //Yükleme tamamlandı

                    setAdapterForLocations();
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
            progress_bar_for_locations.setVisibility(View.GONE);
        }
    }

    private void getCharacterFromNetwork(ArrayList<String> idListOfCharacters) {

        int tmpLength;
        String tmpCharactersList = "https://rickandmortyapi.com/api/character/";
        int tmpUrlCharacterLength = tmpCharactersList.length();

        for (String character : idListOfCharacters) {
            tmpLength = character.length();

            tmpCharactersList += character.substring(tmpUrlCharacterLength, tmpLength) + ", ";
        }

        String charactersList = tmpCharactersList.substring(0, tmpCharactersList.length() - 1);

        progress_bar_for_characters.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(charactersList)
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
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress_bar_for_characters.setVisibility(View.GONE);
                            characters_recycler_view.setVisibility(View.VISIBLE);
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
        characters_recycler_view.setLayoutManager(mLayoutManager);
        characters_recycler_view.setAdapter(adapter);
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public ArrayList<ResultForLocation> getLocationArrayList() {
        return locationArrayList;
    }

    public void setLocationArrayList(ArrayList<ResultForLocation> locationArrayList) {
        this.locationArrayList = locationArrayList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}