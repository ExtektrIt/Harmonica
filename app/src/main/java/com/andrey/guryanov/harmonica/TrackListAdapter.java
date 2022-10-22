package com.andrey.guryanov.harmonica;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.andrey.guryanov.harmonica.utils.App;

import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    private final List<Track> tracks;                                                               //список треков
    private final LayoutInflater inflater;
    private final int COLOR_APP = Color.parseColor("#55dddd");
    private final int COLOR_WHITE = Color.parseColor("#ffffff");

    private ViewHolder currentHolder;                                                               //текущий (выделенный) холдер. нужен для того, чтобы знать, какой холдер уже выделен
    private boolean isFirstSelectedHolder;
    private int viewHolderCount = 0;                                                                //тестовый, считает созданные холдеры и записывает их в атрибуты песни
    private final PlayList playList;


    public TrackListAdapter(Context parent, PlayList playList) {
        this.tracks = playList.getTracks();
        this.inflater = LayoutInflater.from(parent);
        this.playList = playList;
    }

    @NonNull
    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.track_list, parent, false);

        viewHolderCount++;                                                                          //тест

        ViewHolder newHolder = new ViewHolder(view);
        newHolder.trackAttr.setText(String.valueOf(viewHolderCount));                               //тест
        return newHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.trackName.setText(track.getName());

        if (track.getID() == playList.getCurrentTrack().getID()) {                                  //если это текущий трек в плейлисте
            selectHolder(holder);                                                                   //отображаем холдер как выделенный
        }
        else {
            unSelectHolder(holder);                                                                 //иначе отображаем холдер как обычно (не выделенный)
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    private void unSelectHolder(ViewHolder holder) {                                                //метод отменяет выбор холдера и отображает его как обычно (не выделенный)
        holder.layout.setBackground(null);                                                          //сбрасываем цвет холдера на стандартный (зависит от темы) //костыль, но что поделать
        holder.trackName.setTextColor(COLOR_APP);                                                   //меняем цвет названия трека на морской
    }

    private void selectHolder(ViewHolder holder) {                                                  //метод выделяет или отображает холдер как выбранный
        currentHolder = holder;                                                                     //задаём этот холдер как текущий
        holder.layout.setBackgroundColor(COLOR_APP);                                                //меняем цвет холдера на морской
        holder.trackName.setTextColor(COLOR_WHITE);                                                 //меняем цвет названия трека в холдере на белый
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView trackName, trackAttr;
        final ConstraintLayout layout;
        View view;

        ViewHolder(View view){
            super(view);
            trackName = view.findViewById(R.id.tv_track_name);
            trackAttr = view.findViewById(R.id.tv_track_attr);
            this.view = view;
            layout = view.findViewById(R.id.l_track_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();                                     //узнаём позицию нажатого холдера
                    Track track = tracks.get(position);                                             //получаем трек по позиции для взаимодействия с ним
                    if (currentHolder != null) unSelectHolder(currentHolder);                       //если есть выбранный холдер, то отменяем его выбор
                    selectHolder(getThisHolder());                                                  //выбираем текущий нажатый холдер
                    if (track.getID() != playList.getCurrentTrack().getID()) {                      //если выбранный трек не является текущим
                        playList.setCurrentTrack(track);                                            //задаём выбранный трек как текущий в плейлисте
                        App.getPlayer().playFromTrackList();                                        //запускаем текущий трек плейлиста
                    }
                }
            });
        }

        public ViewHolder getThisHolder() {
            return this;
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