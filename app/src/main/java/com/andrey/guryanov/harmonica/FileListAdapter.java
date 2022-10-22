package com.andrey.guryanov.harmonica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private final List<Item> folderItems;
    private final LayoutInflater inflater;
    private final FileListControl controller;


    public FileListAdapter(Context parent, FileListControl controller, List<Item> folderItems) {
        this.folderItems = folderItems;
        this.inflater = LayoutInflater.from(parent);
        this.controller = controller;
    }

    @NonNull
    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.file_list_item, parent, false);
        return new ViewHolder(view);
    }

            /** $
    // сделать для чекбоксов отдельный булевный список*/
    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.ViewHolder holder, int position) {        //метод заполняет данными отображённые вьюхолдеры
        Item item = folderItems.get(position);                                                      //получаем Итем исходя из текущей позиции вьюхолдера для работы с ним
        holder.fileName.setText(item.getName());                                                    //отображаем в холдере имя Итема
        holder.pathFile.setText(item.getPath());                                                    //отображаем в холдере путь Итема
        if (item.isFile()) {                                                                        //если Итем является файлом
            holder.icFolder.setImageResource(android.R.drawable.ic_media_play);                     //то показываем иконку плей в холдере (намекая на то, что это музыкальный файл)
        }
        else {                                                                                      //иначе (если Итем это папка)
            holder.icFolder.setImageResource(android.R.drawable.sym_contact_card);                  //то показываем иконку папки в холдере
        }
        holder.checkBox.setChecked(item.getFlag());                                                 // $
    }

    @Override
    public int getItemCount() {
        return folderItems.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icFolder;
        final TextView fileName, pathFile;
        final CheckBox checkBox;
        ViewHolder(View view){
            super(view);
            icFolder = view.findViewById(R.id.iv_ic_folder);
            fileName = view.findViewById(R.id.tv_file_name);
            pathFile = view.findViewById(R.id.tv_path_file);
            checkBox = view.findViewById(R.id.cb);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();                                     //получаем позицию этого холдера
                    if (folderItems.get(position).isFile()) {                                       //если Итем выбранного холдера является файлом
                        Item item = folderItems.get(position);                                      //извлекаем Итем по позиции для работы с ним
                        checkBox.setChecked(item.replaceFlag());                                    // $ //выделяем этот файл и помечаем чекбокс этого холдера
                    }
                    else {                                                                          //иначе (если Итем это папка)
                        for(Item item : folderItems) {
                            item.setFlag(false);                                                    // $
                        }
                        controller.openFolder(position);                                            //открываем папку (вызываем метод, который перерисовывает ресайклервью с новыми папками)
                    }
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {                                //вешаем слушатель на чекбокс у холдера
                @Override
                public void onClick(View v) {
                    folderItems.get(getAbsoluteAdapterPosition()).replaceFlag();                    // $
                }
            });
        }
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//    private final Context parent;
//    private final RecyclerView fileList;
//    private final View view;

//    private String currentPath;

//    public FileListAdapter(Context parent, View view, RecyclerView fileList, List<Item> files) {
//        this.files = files;
//        this.parent = parent;
//        this.inflater = LayoutInflater.from(parent);
//        this.fileList = fileList;
//        this.view = view;
//    }