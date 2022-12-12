package com.andrey.guryanov.harmonica;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.andrey.guryanov.harmonica.utils.App;
import com.andrey.guryanov.harmonica.utils.Computer;

import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    private final List<Track> tracks;                                                               //список треков
    private final LayoutInflater inflater;
    private final int COLOR_APP = Color.parseColor("#55dddd");
    private final int COLOR_WHITE = Color.parseColor("#ffffff");

    private final List<ViewHolder> holders;
    private final RecyclerView recyclerView;
    private final TrackList trackList;
    private ViewHolder selectedHolder;                                                              //текущий (выделенный) холдер. Нужен для того, чтобы знать, какой холдер уже выделен
    private boolean scrolled = false;
//    private boolean holderHeightComputed = false;
//    private byte iterator = 0;
//    private int holderHeight;
    //private boolean isFirstSelectedHolder;
    private int viewHolderCount = 0;   //                                                             //считает созданные холдеры
    ////private final PlayList playList;
    private final Player player;
    private TrackListViewReadyListener readyListener;


    public TrackListAdapter(Context parent, PlayList playList, RecyclerView listView, TrackList trackList) {
        this.tracks = playList.getTracks();
        this.inflater = LayoutInflater.from(parent);
        ////this.playList = playList;
        this.player = App.getPlayer();
        this.trackList = trackList;
        holders = new ArrayList<>();
        recyclerView = listView;

        readyListener = new TrackListViewReadyListener(2000);

    }

    @NonNull
    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.track_list, parent, false);

        ViewHolder holder = new ViewHolder(view);
        holders.add(holder);

        if (viewHolderCount == 1) {
//            startReadyListener();
            readyListener.start();
        }
        viewHolderCount++;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.trackName.setText(name(track, position));
        holder.trackAttr.setText(attributes(track));

        if (player.getCurrentTrackNumber() == position) {                                           //если это текущий трек в плейлисте
            holder.showSelect();                                                                //отображаем холдер как выделенный
        }
        else holder.showUnselect();                                                         //иначе отображаем холдер как обычно (не выделенный)
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }


    /** ГЕТТЕРЫ */


    public int getHolderHeight() {
        return holders.get(0).getHeight();
    }

    public ViewHolder getSelectedHolder() {
        return selectedHolder;
    }

    public List<ViewHolder> getHolders() {
        return holders;
    }

    public int getHoldersCount() {
        return holders.size();
    }


    /** ПУБЛИЧНЫЕ СЛУЖЕБНЫЕ МЕТОДЫ */

        /** $ */
    public void reselectHolder() {
        getSelectedHolder().showUnselect();
        scrolled = false;
//        int module = player.getPrevTrackNumber() - player.getCurrentTrackNumber();
        if (Computer.digitModule(player.getPrevTrackNumber()
                - player.getCurrentTrackNumber()) >= holders.size()) {
            return;
        }
        for (ViewHolder holder : holders) {
            if (holder.getBindingAdapterPosition() == player.getCurrentTrackNumber()) {
                holder.showSelect();
            }
        }
    }

    public void smoothScrollToTrack() {
        recyclerView.smoothScrollBy(0, computeTrackPosition());
    }

    public void scrollToTrack() {
        recyclerView.scrollBy(0, computeTrackPosition());
    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    private void startReadyListener() {
        if (player.getCurrentTrackNumber() > 1) readyListener.start();
    }

    private String name(Track track, int number) {
        return "#" + (number + 1) + ": " + track.getName();
    }

    private String attributes(Track track) {
        return "[" + track.getExtension().toUpperCase() + "] :: [" + track.etSizeMB() + " MB]";
    }

    private int computeTrackPosition() {
        int screenHeight = recyclerView.getMeasuredHeight();
        int holderHeight = holders.get(0).getHeight();
        int offset = recyclerView.computeVerticalScrollOffset();
        scrolled = true;
        return holderHeight * player.getCurrentTrackNumber()
                - screenHeight / 2 + holderHeight / 2 - offset;
    }

    private void firstScroll() {
        recyclerView.scrollBy(0, computeTrackPosition() - (viewHolderCount / 2 * getHolderHeight()));
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView trackName, trackAttr, duration;
        final ConstraintLayout layout;
        boolean selected;// *
//        View view;

        ViewHolder(View view){
            super(view);
            trackName = view.findViewById(R.id.tv_track_name);
            trackAttr = view.findViewById(R.id.tv_track_attr);
            duration = view.findViewById(R.id.tv_duration);
//            this.view = view;
            layout = view.findViewById(R.id.l_track_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();                                     //узнаём позицию нажатого холдера
                    ////Track track = tracks.get(position);                                             //получаем трек по позиции для взаимодействия с ним
//                    if (currentHolder != null) unSelectHolder(currentHolder);                       //если есть выбранный холдер, то отменяем его выбор
//                    selectHolder(getThisHolder());                                                  //выбираем текущий нажатый холдер
                    if (selectedHolder != null) selectedHolder.showUnselect();
                    showSelect();
                    if (player.getCurrentTrackNumber() != position) {                               //если выбранный трек не является текущим
                        player.playFromTrackList(position);                                                 //запускаем текущий трек плейлиста
                    }
                    trackList.changePlayButton();
                }
            });
        }

        public ViewHolder getThisHolder() {
            return this;
        }

//        public View getThisView() {
//            return view;
//        }

        public int getHeight() {
            return layout.getHeight();
        }

        public void showUnselect() {                                                //метод отменяет выбор холдера и отображает его как обычно (не выделенный)
            layout.setBackground(null);                                                          //сбрасываем цвет холдера на стандартный (зависит от темы) //костыль, но что поделать
            trackName.setTextColor(COLOR_APP);                                                   //меняем цвет названия трека на морской
        }

        private void showSelect() {                                                  //метод выделяет или отображает холдер как выбранный
            selectedHolder = getThisHolder();                                                                     //задаём этот холдер как текущий
            layout.setBackgroundColor(COLOR_APP);                                                //меняем цвет холдера на морской
            trackName.setTextColor(COLOR_WHITE);                                                 //меняем цвет названия трека в холдере на белый
        }


    }


    private class TrackListViewReadyListener extends CountDownTimer {
        int counter = 0;

        public TrackListViewReadyListener(int millisInFuture) {
            super(millisInFuture, 50L);
        }

        @Override
        public void onFinish() {//когда счётчик (таймер) достигает нуля
            readyListener.cancel();
        }

        @Override
        public void onTick(long millisUntilFinished) {                                              //сам счётчик, выполняет команды раз в
            int counter2 = recyclerView.getMeasuredHeight();
            if (counter == counter2) {
                firstScroll();//scrollToTrack();
                smoothScrollToTrack();
                onFinish();
            }
            counter = counter2;

        }
    }


}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//holder.songAttr.setText(String.valueOf(viewHolderCount));  //test
//holder.songAttr.setBackgroundColor(holder.layout.getSolidColor());
//holder.layout.getBackground().

//            holder.layout.setBackgroundColor(Color.parseColor("#55dddd"));   //цвет "app"
//            holder.songName.setTextColor(Color.parseColor("#ffffff"));  //белый
//            //holder.layout.setBackground();
//            //holder.songName.setActivated(true);
//
//            currentHolder = holder;

//holder.songName.setSelected(true);


//holder.layout.setBackgroundColor(R.color.background_theme);
//holder.layout.setBackgroundColor(R.color.white);   //пока что стандартный цвет ячейки будет таким, в будущем сделаю цвет текущей темы
//        holder.checkBox.setChecked(item.getFlag());
//        holder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                folderItems.get(holder.getAbsoluteAdapterPosition()).replaceFlag();
////                Toast toast = Toast.makeText(inflater.getContext(), "нажат чекбокс № " + holder.getAbsoluteAdapterPosition(), Toast.LENGTH_SHORT);
////                toast.show();
//            }
//        });

//layout.setBackgroundColor(R.color.app);
//songName.setTextColor(R.color.white);
//empty.setBackgroundColor(R.color.app);

//currentHolder.

//            holder.layout.setBackground(null); //КОСТЫЛЬ! задаём стандартный фон (видимо, в методе встроена проверка на нуль, что очень хорошо)
//            holder.songName.setTextColor(Color.parseColor("#55dddd"));

//        public void selectSong() {
//            unSelectOldSong();
//            selectThisSong();
//        }

//                    if (track.getID() != player.getCurrentTrack().getID()) {                      //если выбранный трек не является текущим
//                    player.setCurrentTrack(track);                                            //задаём выбранный трек как текущий в плейлисте
//                    App.getPlayer().playFromTrackList();                                        //запускаем текущий трек плейлиста
//player.setCurrentTrackNumber(position);                                       //задаём выбранный трек как текущий в плейлисте


//    public void selectHolder() {
//        selectedHolder.showSelect();
//        if ( ! scrolled) scrollToPlayed();
//    }

//    private void unSelectHolder(ViewHolder holder) {                                                //метод отменяет выбор холдера и отображает его как обычно (не выделенный)
//        holder.layout.setBackground(null);                                                          //сбрасываем цвет холдера на стандартный (зависит от темы) //костыль, но что поделать
//        holder.trackName.setTextColor(COLOR_APP);                                                   //меняем цвет названия трека на морской
//    }

//    private void selectHolder(ViewHolder holder) {                                                  //метод выделяет или отображает холдер как выбранный
//        selectedHolder = holder;                                                                     //задаём этот холдер как текущий
//        holder.layout.setBackgroundColor(COLOR_APP);                                                //меняем цвет холдера на морской
//        holder.trackName.setTextColor(COLOR_WHITE);                                                 //меняем цвет названия трека в холдере на белый
//    }

//        holder.duration.setText(track.getDuration());

//        if (track.getID() == player.getCurrentTrack().getID()) {                                  //если это текущий трек в плейлисте
//            selectHolder(holder);                                                                   //отображаем холдер как выделенный
//        }
//        else {
//            unSelectHolder(holder);                                                                 //иначе отображаем холдер как обычно (не выделенный)
//        }

//        String name = (position + 1) + ": " + track.getName();
//        String attributes = "[" + track.getExtension().toUpperCase() + "] [" +
//                track.getSizeMB() + "]";