package com.example.android.alcintermedchallenge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    MyAdapterClass myAdapter;
    ProgressDialog loading;
    ProgressBar load;
    List<Users> users;

    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isDeviceConnected(MainActivity.this)) buildDialog(MainActivity.this).show();

        recyclerView = (RecyclerView) findViewById(R.id.user_each);
        recyclerView.setHasFixedSize(true);
        load = (ProgressBar) findViewById(R.id.load);
        loading = new ProgressDialog(MainActivity.this);

        users = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_content);

        setSwipeRefreshLayout();
        addOnScrollListener();
        new myTask().execute();
    }
    public boolean isDeviceConnected(Context context){
        ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo dNetInfo = connect.getActiveNetworkInfo();
        return dNetInfo != null && dNetInfo.isConnected();
    }
    public AlertDialog.Builder buildDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Connection error");
        builder.setMessage("Verify you have an existing internet connection, click \"OK\" to Quit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        return builder;
    }

    private class myTask extends AsyncTask <String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            super.onPreExecute();
            loading.setTitle("Java Developers in Lagos");
            loading.setMessage("Retrieving...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url("https://api.github.com/search/users?q=+location:lagos+language:java&page="+page);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();

                JSONObject resp = new JSONObject(response.body().string());
                JSONArray items = resp.getJSONArray("items");

                for (int i=0; i<items.length(); i++){
                    JSONObject object = items.getJSONObject(i);

                    String userName = object.getString("login");

                    String serverProfilePicUrl = object.getString("avatar_url");

                    String uLink = object.getString("html_url");

                    Users githubUsers = new Users(serverProfilePicUrl, userName, uLink);
                    users.add(githubUsers);
                }
                page = page +1;
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }catch (JSONException e){
                e.getMessage();
                return e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loading.dismiss();
            recyclerView = (RecyclerView) findViewById(R.id.user_each);
            myAdapter = new MyAdapterClass(MainActivity.this, users);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            myAdapter.notifyDataSetChanged();
        }
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new myTask().execute();
            }
        });
    }
    public void addOnScrollListener(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(1)) {
                    page = page++;
                    loading.dismiss();
                    new myTask().execute();
                }
            }
        });
    }
}