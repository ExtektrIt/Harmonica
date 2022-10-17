package com.andrey.guryanov.harmonica;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private final List<Item> folderItems;
    private final LayoutInflater inflater;

    private FileListControl controller;

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

    public FileListAdapter(Context parent, FileListControl controller, List<Item> folderItems) {
        //this.parent = parent;
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

    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.ViewHolder holder, int position) {
        Item item = folderItems.get(position);
        holder.fileName.setText(item.getName());
        holder.pathFile.setText(item.getPath());
        if (item.isFile()) {
            holder.icFolder.setImageResource(android.R.drawable.ic_media_play);
        }
        else {
            holder.icFolder.setImageResource(android.R.drawable.sym_contact_card);
        }
        holder.checkBox.setChecked(item.getFlag());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderItems.get(holder.getAbsoluteAdapterPosition()).replaceFlag();
//                Toast toast = Toast.makeText(inflater.getContext(), "нажат чекбокс № " + holder.getAbsoluteAdapterPosition(), Toast.LENGTH_SHORT);
//                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderItems.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icFolder;
        final TextView fileName, pathFile;
        final CheckBox checkBox;
//        final View layout;
        ViewHolder(View view){
            super(view);
            icFolder = view.findViewById(R.id.iv_ic_folder);
            fileName = view.findViewById(R.id.tv_file_name);
            pathFile = view.findViewById(R.id.tv_path_file);
            checkBox = view.findViewById(R.id.cb);
//            layout = view.findViewById(R.id.l_file_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    //controller.clickOnViewHolder(position);

//                    if (view.getId() == R.id.cb) {
//                        Item item = folderItems.get(position);
//                        item.setFlag(false);
//                        Toast toast = Toast.makeText(inflater.getContext(), "111!", Toast.LENGTH_SHORT);
//                        toast.show();
//                    }

                    if (folderItems.get(position).isFile()) {
                        Item item = folderItems.get(position);
                        checkBox.setChecked(item.replaceFlag());
                    }
                    else {
                        for(Item item : folderItems) {
                            item.setFlag(false);
                        }
                        controller.goToNext(position);
                    }

//                    Toast toast = Toast.makeText(parent, "Была нажата позиция " + position, Toast.LENGTH_SHORT);
//                    toast.show();

                    //reCreateFileList(position);

                    //ItemListFragment.getCurrentAdapter().reCreateFileList(position);

//                    FileListAdapter adapter = new FileListAdapter(this,)
//                    Fragment fragment = new ItemListFragment(position);
                }
            });


        }
    }

}
