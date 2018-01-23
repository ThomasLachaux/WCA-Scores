package com.adrastel.niviel.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrastel.niviel.assets.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecyclerViewCompat extends RecyclerView {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerViewCompat recyclerView = this;

    private OkHttpClient client = new OkHttpClient();

    public RecyclerViewCompat(Context context) {
        super(context);
    }

    public RecyclerViewCompat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewCompat(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initRecyclerViewCompat(SwipeRefreshLayout swipeRefreshLayout, ProgressBar progressBar, TextView emptyView) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.progressBar = progressBar;
        this.emptyView = emptyView;
    }

    public void hideAll() {
        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if(emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if(progressBar != null) {
            progressBar.setVisibility(GONE);
        }
        if(recyclerView != null) {
            recyclerView.setVisibility(GONE);
        }
    }

    public void showRecycler() {
        hideAll();
        if(recyclerView != null) {
            recyclerView.setVisibility(VISIBLE);
        }
    }

    public void showProgress() {
        if(getAdapter() != null && getAdapter().getItemCount() != 0) {
            showRecycler();
        }

        else if(!swipeRefreshLayout.isRefreshing()){
            hideAll();
            if(progressBar != null) {
                progressBar.setVisibility(VISIBLE);
            }
        }
    }

    public void showEmpty() {
        if(getAdapter() != null && getAdapter().getItemCount() != 0) {
            showRecycler();
        }

        else {
            hideAll();
            if(emptyView != null) {
                emptyView.setVisibility(VISIBLE);
            }
        }
    }

    public void callData(HttpUrl url, final SuccessCallback callback) {
        showProgress();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        showEmpty();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showEmpty();
                        }
                    });
                    Log.e("Http error " + response.code());
                }

                else {
                    callback.onSuccess(response.body().string());
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showRecycler();
                        }
                    });
                }

                response.close();
            }
        });
    }

    public interface SuccessCallback {
        void onSuccess(String response);
    }


}
