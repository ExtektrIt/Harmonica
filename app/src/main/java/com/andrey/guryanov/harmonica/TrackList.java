package com.andrey.guryanov.harmonica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.andrey.guryanov.harmonica.utils.App;

public class TrackList extends AppCompatActivity {

    private App app;
    private PlayList playList;
    private RecyclerView trackList;
    private TextView playListName, countTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_track_list);

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
        trackList = findViewById(R.id.rv_track_list);
        playListName = findViewById(R.id.tv_playlist_name);
        countTracks = findViewById(R.id.tv_count_tracks);

        playListName.setText(playList.getName());
        countTracks.setText(String.valueOf(playList.getCountTracks()));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        trackList.setLayoutManager(layoutManager);
    }

    public void startAdapter() {
        TrackListAdapter adapter = new TrackListAdapter(this, playList);
        trackList.setAdapter(adapter);
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