package com.andrey.guryanov.harmonica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrey.guryanov.harmonica.utils.App;

public class TrackList extends AppCompatActivity {

    private Player player;
    private PlayList playList;
    private RecyclerView recyclerView;
    private TextView playListName, countTracks, trackName, test;
    private ImageButton prev, play, next;
    private ConstraintLayout holder;


    private RecyclerView.LayoutManager manager;//
    private TrackListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        App.setCurrentLocation(this.getLocalClassName());
        player = App.getPlayer();
        playList = player.getCurrentPlayList();
        initViews();
//        player.changePlayButton(play);// *
        changePlayButton();

        playLongListener();
        nextLongListener();
        prevLongListener();

        startAdapter();
//        scrollToTrack();
//        test();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.setCurrentLocation(this.getLocalClassName());
    }


    /** ИНИЦИАЛИЗАЦИЯ */


    protected void initViews() {
        recyclerView = findViewById(R.id.rv_track_list);
        playListName = findViewById(R.id.tv_playlist_name);
        countTracks = findViewById(R.id.tv_count_tracks);
        test = findViewById(R.id.tv_play_mode_track_list);
        trackName = findViewById(R.id.tv_track_name_track_list);
        prev = findViewById(R.id.ib_prevTrack_button_track_list);
        play = findViewById(R.id.ib_playTrack_button_track_list);
        next = findViewById(R.id.ib_nextTrack_button_track_list);

        holder = findViewById(R.id.l_track_list);

        playListName.setText(playList.getName());
        countTracks.setText(String.valueOf(playList.getCountTracks()));
        if (player.getCurrentTrackNumber() >= 0) {
            trackName.setText(player.getCurrentTrack().getName());
            trackName.setSelected(true);
        }

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    private void startAdapter() {
        adapter = new TrackListAdapter(this, playList, recyclerView, this);
        player.setTrackListData(adapter, this);////
        recyclerView.setAdapter(adapter);
        if (player.getCurrentTrackNumber() >= 0) {
//            trackList.scrollToPosition(adapter.getHoldersCount() / -2);//(player.getCurrentTrackNumber());

//            if (adapter.itemHeightIsComputed()) {
//                adapter.scrollToPlayed();
//            }
//            if (adapter.getHolderHeight() > 0) {
//                adapter.scrollToPlayed();
//            }
//            else trackList.scrollToPosition(player.getCurrentTrackNumber());
        }
    }

    public void test(){
        RecyclerView r = recyclerView;
        TrackListAdapter tr = adapter;
//        manager.scrollToPosition(1);

//        int h = holder.getBottom();
    }


    /** СЛУШАТЕЛИ */


    private void playLongListener() {
        play.setOnLongClickListener(v -> {
            if (player.getCurrentTrackNumber() >= 0) {
                adapter.scrollToTrack();
            }
            return true;
        });
    }

    private void nextLongListener() {
        next.setOnLongClickListener(v -> {
            player.rewindTrackTo10Sec(1);
            return true;
        });
    }

    private void prevLongListener() {
        prev.setOnLongClickListener(v -> {
            player.rewindTrackTo10Sec(-1);
            return true;
        });
    }


    /** ТОЧКИ ВЗАИМОДЕЙСТВИЯ (ИНТЕРФЕЙС ПОЛЬЗОВАТЕЛЯ) */


    public void goToFileList(View v) {                                                              //переход в проводник для добавления треков в плейлист
        Intent intent = new Intent(this, FileList.class);
        startActivity(intent);
    }

    public void goToTitleScreen(View view) {
        super.onBackPressed();
    }

    public void prev(View v) {
        player.prev();
//        player.reselectTrack();
//        adapter.reselectHolder();
//        adapter.scrollToPlayed();
    }

    public void play(View v) {
        player.playFromMain();
        changePlayButton();
//        player.changePlayButton(play);
    }

    public void next(View v) {
        player.next();
//        player.reselectTrack();
//        adapter.reselectHolder();
//        adapter.scrollToPlayed();
    }

    public void testFunc(View v) {
        test.setText(String.valueOf(recyclerView.computeVerticalScrollOffset())
                + " " + String.valueOf(recyclerView.computeVerticalScrollExtent())
                + " " + computeScroll(0) + " | " + recyclerView.getMeasuredHeight());
//        adapter.scrollToPlayed();
//        int s = trackList.getScrollState();

    }


    /** СЕТТЕРЫ */


    public void setTrackName(String name) {
        trackName.setText(name);
    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    public void changePlayButton() {  // *                                                              //метод меняет иконку кнопки Плей в зависимости от состояния проигрывателя
        if (player.isPlaying()) play.setImageResource(android.R.drawable.ic_media_pause);
        else play.setImageResource(android.R.drawable.ic_media_play);
    }

    public void scrollToTrack() {
        recyclerView.scrollTo(0, 1000);
    }

    public void reSelectTrack() {

    }

    public int computeScroll(int arg) {//test
        if (arg == 0) return ((recyclerView.computeVerticalScrollExtent() / -2) + (adapter.getHolderHeight() * (player.getCurrentTrackNumber() + 1)) - (adapter.getHolderHeight() / 2));
        else return ((recyclerView.computeVerticalScrollExtent() / -2) + (adapter.getHolderHeight() * (player.getCurrentTrackNumber() - player.getPrevTrackNumber() + 1)) - (adapter.getHolderHeight() / 2));
    }



}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//    public void scroll() {
////        trackList.scrollBy(0, ((trackList.computeVerticalScrollExtent() / -2) + (adapter.getHolderHeight() * (player.getCurrentTrackNumber() + 1)) - (adapter.getHolderHeight() / 2)));
////        trackList.scrollBy(0, ((trackList.computeVerticalScrollExtent() / -2) + (adapter.getHolderHeight() * (player.getCurrentTrackNumber() + 1)) - 54));
////        trackList.scrollToPosition(player.getCurrentTrackNumber() - adapter.getHoldersCount() / 2);
////        adapter.scrollToPlayed();
//    }

//trackList.scrollToPosition(player.getCurrentTrackNumber() - (trackList.getHeight() / adapter.getHolderHeight()) / 2);

//scroll();
//            trackList.scrollToPosition(player.getCurrentTrackNumber() - App.getCountHolders() / 2);
//            //int uhg = adapter.getHolderHeight();
//            int count = trackList.getHeight() / adapter.getHolderHeight();
//            trackList.scrollToPosition(player.getCurrentTrackNumber() - count);
//            int hugg = trackList.getScrollState();
//trackList.scrollToPosition(player.getCurrentTrackNumber() - App.getCountHolders() / 2);
//            trackList.scrollBy(0, player.getCurrentTrackNumber() * 100);