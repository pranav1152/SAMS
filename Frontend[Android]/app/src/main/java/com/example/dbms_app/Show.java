package com.example.dbms_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Show extends AppCompatActivity {
    Retrofit retrofit;
    Requests requests;
    SwipeRefreshLayout srl;
    RecyclerView rv;
    Callbacks callback;
    ArrayList<Subjects> subjects;
    SharedPreferences sp;
    ImageView logout;
    TextView uidtv;
    private final String prefs = "MYPREFS";
    Connect connect = Launcher.connect;
    Context context ;
    Adapter adapter;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        uidtv = findViewById(R.id.uidtv);
        srl = findViewById(R.id.refresh);
        logout = findViewById(R.id.logout);
        rv = findViewById(R.id.rv);


        context = this;
        subjects = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        adapter = new Adapter(this, subjects);
        rv.setAdapter(adapter);

        sp = getSharedPreferences(prefs, MODE_PRIVATE);
        uid = sp.getString("uid", "");
        uidtv.setText(uid.toUpperCase());

        callback = subs -> {
            subjects.clear();
            subjects.addAll(subs);
            adapter.notifyDataSetChanged();
        };

        srl.setOnRefreshListener(() -> {
            connect.get_attendance(uid,callback, context);
            srl.setRefreshing(false);
        });

        loadAttendance();
        logout.setOnClickListener(v -> log_out());
    }

    public void loadAttendance(){
        if(!checkConnection()) {
            Gson gson = new Gson();
            String json = sp.getString("Json", "");
            if (json.length() != 0) {
                Type type = new TypeToken<ArrayList<Subjects>>() {
                }.getType();
                ArrayList<Subjects> backup = gson.fromJson(json, type);
                rv.setAdapter(new Adapter(this, backup));
                adapter.notifyDataSetChanged();
            }
        }
        else{
            connect.get_attendance(uid,callback,context);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor1 = sp.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(subjects);
        editor1.putString("Json",json1);
        editor1.apply();
    }

    public void log_out(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFirst", true);
        editor.putString("username", "");
        editor.putString("uid", "");
        editor.apply();
        Intent i = new Intent(Show.this, Launcher.class);
        startActivity(i);
        finish();
    }
    private boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}