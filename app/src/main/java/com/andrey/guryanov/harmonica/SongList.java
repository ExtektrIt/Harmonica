package com.andrey.guryanov.harmonica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.andrey.guryanov.harmonica.utils.App;

public class SongList extends AppCompatActivity {

    private App app;
    private PlayList playList;
    private RecyclerView songList;
    private TextView playListName, countSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_song_list);

        playList = App.getPlayer().getCurrentPlayList();

        //this.playList = App.getPlayer().getCurrentPlayList();
        initViews();
        //getBundleArgs();

        startAdapter();

    }

//    protected void getBundleArgs() {  //test
//        Bundle arguments = getIntent().getExtras();
//
//        PlayList playList;
//        if (arguments != null) {
//            playList = (PlayList) arguments.getSerializable("PlayList");
//            this.playList = playList;
//        }
//    }

    protected void initViews() {
        songList = findViewById(R.id.rv_song_list);
        playListName = findViewById(R.id.tv_playlist_name);
        countSongs = findViewById(R.id.tv_count_songs);

        playListName.setText(playList.getName());
        countSongs.setText(String.valueOf(playList.getCountSongs()));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        songList.setLayoutManager(layoutManager);
    }

    public void startAdapter() {
        SongListAdapter adapter = new SongListAdapter(this, playList);
        songList.setAdapter(adapter);
    }

    public void refresh(View view) {
        startAdapter();
    }

    public void goToTitleScreen(View view) {
        super.onBackPressed();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

}