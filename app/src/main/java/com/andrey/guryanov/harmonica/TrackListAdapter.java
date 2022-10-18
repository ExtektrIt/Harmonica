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

    private final List<Track> tracks;
    private final LayoutInflater inflater;
    private final Context parent;
    private final int COLOR_APP = Color.parseColor("#55dddd");
    private final int COLOR_WHITE = Color.parseColor("#ffffff");

    private ViewHolder currentHolder;   //текущий (выделенный) ВьюХолдер
    //private Drawable defaultColor;

    //private int currentPosition;    = -1; //в последствии заменю на булеву. дает понять, есть ли вообще в СонгЛисте выделенный ВьюХолдер
    private boolean isFirstSelectedHolder;
    private int viewHolderCount = 0;    //тестовый, считает созданные холдеры и записывает их в атрибуты песни
    //private int none = -1;

    //private Player player;
    private final PlayList playList;


    public TrackListAdapter(Context parent, PlayList playList) {
        this.parent = parent;
        this.tracks = playList.getTracks();
        this.inflater = LayoutInflater.from(parent);
        //this.player = player;
        this.playList = playList;
        //currentPosition = -1;

    }

    @NonNull
    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.track_list, parent, false);

        viewHolderCount++;  //тест

        ViewHolder newHolder = new ViewHolder(view);
        newHolder.trackAttr.setText(String.valueOf(viewHolderCount));
        return newHolder;
        //return new ViewHolder(view, viewHolderCount);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.ViewHolder holder, int position) {
        Track track = tracks.get(position);
        //holder.songName.setText("Песня № " + position);//(track.getName());
        holder.trackName.setText(track.getName());
        //holder.songAttr.setText(String.valueOf(viewHolderCount));  //test
        //holder.songAttr.setBackgroundColor(holder.layout.getSolidColor());
        //holder.layout.getBackground().



        if (track.getID() == playList.getCurrentTrack().getID()) {                                    //если это текущая песня в плейлисте
            selectHolder(holder);
//            holder.layout.setBackgroundColor(Color.parseColor("#55dddd"));   //цвет "app"
//            holder.songName.setTextColor(Color.parseColor("#ffffff"));  //белый
//            //holder.layout.setBackground();
//            //holder.songName.setActivated(true);
//
//            currentHolder = holder;

            //holder.songName.setSelected(true);

        }
        else {
            unSelectHolder(holder);
//            holder.layout.setBackground(null); //КОСТЫЛЬ! задаём стандартный фон (видимо, в методе встроена проверка на нуль, что очень хорошо)
//            holder.songName.setTextColor(Color.parseColor("#55dddd"));
        }

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
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void unSelectHolder(ViewHolder holder) {
        //currentHolder.
        holder.layout.setBackground(null);
        holder.trackName.setTextColor(COLOR_APP);
    }

    public void selectHolder(ViewHolder holder) {
        currentHolder = holder;
        holder.layout.setBackgroundColor(COLOR_APP);
        holder.trackName.setTextColor(COLOR_WHITE);
        //layout.setBackgroundColor(R.color.app);
        //songName.setTextColor(R.color.white);
        //empty.setBackgroundColor(R.color.app);

        //currentHolder.
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView trackName, trackAttr;
        //final View layout;
        final ConstraintLayout layout;
        View view, empty;

        ViewHolder(View view){

            super(view);
            trackName = view.findViewById(R.id.tv_track_name);
            trackAttr = view.findViewById(R.id.tv_track_attr);

            this.view = view;
            layout = view.findViewById(R.id.l_track_list);
            //empty = view.findViewById(R.id.v_song_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    Track track = tracks.get(position);

                    if (currentHolder != null) unSelectHolder(currentHolder);                       //если есть выбранный холдер, то отменяем его выбор
                    selectHolder(getThisHolder());

                    if (track.getID() != playList.getCurrentTrack().getID()) {                        //если выбранная песня не является текущей
                        playList.setCurrentTrack(track);                                              //задаём выбранную песню как текущую в плейлисте
                        App.getPlayer().playFromTrackList();                                                     //запускаем текущую песню плейлиста
                    }



//                    Toast t = Toast.makeText(inflater.getContext(), "Песня запустилась! Ля-ля", Toast.LENGTH_SHORT);
//                    t.show();

                }
            });


        }
        public ViewHolder getThisHolder() {
            return this;
        }

//        public void selectSong() {
//            unSelectOldSong();
//            selectThisSong();
//        }



    }

}
