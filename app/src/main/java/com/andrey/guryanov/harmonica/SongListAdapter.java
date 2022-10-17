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

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private final List<Song> songs;
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


    public SongListAdapter(Context parent, PlayList playList) {
        this.parent = parent;
        this.songs = playList.getSongs();
        this.inflater = LayoutInflater.from(parent);
        //this.player = player;
        this.playList = playList;
        //currentPosition = -1;

    }

    @NonNull
    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_list, parent, false);

        viewHolderCount++;  //тест

        ViewHolder newHolder = new ViewHolder(view);
        newHolder.songAttr.setText(String.valueOf(viewHolderCount));
        return newHolder;
        //return new ViewHolder(view, viewHolderCount);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        //holder.songName.setText("Песня № " + position);//(song.getName());
        holder.songName.setText(song.getName());
        //holder.songAttr.setText(String.valueOf(viewHolderCount));  //test
        //holder.songAttr.setBackgroundColor(holder.layout.getSolidColor());
        //holder.layout.getBackground().



        if (song.getID() == playList.getCurrentSong().getID()) {                                    //если это текущая песня в плейлисте
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
        return songs.size();
    }

    public void unSelectHolder(ViewHolder holder) {
        //currentHolder.
        holder.layout.setBackground(null);
        holder.songName.setTextColor(COLOR_APP);
    }

    public void selectHolder(ViewHolder holder) {
        currentHolder = holder;
        holder.layout.setBackgroundColor(COLOR_APP);
        holder.songName.setTextColor(COLOR_WHITE);
        //layout.setBackgroundColor(R.color.app);
        //songName.setTextColor(R.color.white);
        //empty.setBackgroundColor(R.color.app);

        //currentHolder.
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView songName, songAttr;
        //final View layout;
        final ConstraintLayout layout;
        View view, empty;

        ViewHolder(View view){

            super(view);
            songName = view.findViewById(R.id.tv_song_name);
            songAttr = view.findViewById(R.id.tv_song_attr);

            this.view = view;
            layout = view.findViewById(R.id.l_song_list);
            //empty = view.findViewById(R.id.v_song_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    Song song = songs.get(position);

                    if (currentHolder != null) unSelectHolder(currentHolder);                       //если есть выбранный холдер, то отменяем его выбор
                    selectHolder(getThisHolder());

                    if (song.getID() != playList.getCurrentSong().getID()) {                        //если выбранная песня не является текущей
                        playList.setCurrentSong(song);                                              //задаём выбранную песню как текущую в плейлисте
                        App.getPlayer().playFromSongList();                                                     //запускаем текущую песню плейлиста
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
