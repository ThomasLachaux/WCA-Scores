package com.adrastel.niviel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.SearchAdapter;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.models.readable.Suggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private OkHttpClient client = new OkHttpClient();

    private SearchAdapter adapter;

    public static final int SEARCH_SUCCESS = 50;
    public static final String NAME = "name";
    public static final String WCA_ID = "wca_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        adapter = new SearchAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setIconifiedByDefault(true);
        searchView.setIconified(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText != null && !newText.equalsIgnoreCase(""))
                    callData(newText);
                Log.d(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callData(String query) {


        HttpUrl url = new WcaUrl()
                .apiSearch(query)
                .build();



        Request request = new Request.Builder()
                .url(url)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final ArrayList<Suggestion> suggestions = new ArrayList<>();
                try {
                    if(!response.isSuccessful()) {
                        Log.e("HTTP Error");
                        return;
                    }

                    String data = response.body().string();
                    response.close();

                    JSONObject tree = new JSONObject(data);
                    JSONArray result = tree.getJSONArray("result");

                    suggestions.clear();

                    for (int i = 0; i < result.length(); i++) {


                        JSONObject user = result.getJSONObject(i);

                        String type = "";
                        String name = "";
                        String wca_id = "";

                        if(user != null) {
                            try {
                                type = user.getString("class");
                                name = user.getString("name");
                                wca_id = user.getString("wca_id");
                            }
                            catch (Exception e) {
                                Log.e("Not a person");
                            }
                        }

                        if((type.equals("person") || type.equals("suggestionUser")) && wca_id != null) {

                            suggestions.add(new Suggestion(name, wca_id));
                            i++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(suggestions);
                    }
                });

            }
        });

    }


}
