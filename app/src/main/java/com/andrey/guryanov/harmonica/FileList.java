package com.andrey.guryanov.harmonica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrey.guryanov.harmonica.utils.Permission;

import java.util.ArrayList;
import java.util.List;

public class FileList extends AppCompatActivity {

    private static final int PERMISSION_STORAGE = 100;

    private RecyclerView fileList;
    private TextView folderName, folderPath;
    private ImageButton goBack, goToTitleScreen, btnCheckAll, btnDone;
    private List<Object> args;
//    private String playListName;
//    private String name, path;
//    private Context thisContext;//тест
    private FileListControl controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        requestPermissionsToReadFS();

        initViews();
        initRecyclerView();
        initArgs();
        initController();

        showHomeFolders();
    }

    private void requestPermissionsToReadFS() {                                                     //метод запрашивает разрешение на чтение файловой системы, если оно ещё не предоставлено
        if (Permission.hasPermissions(this)) return;
        Permission.requestPermissions(this, PERMISSION_STORAGE);
    }


    /** ИНИЦИАЛИЗАЦИЯ ПЕРЕМЕННЫХ */


    private void initViews () {
        fileList = findViewById(R.id.rv_file_list);
        folderName = findViewById(R.id.tv_folder_name);
        folderPath = findViewById(R.id.tv_folder_path);
        goBack = findViewById(R.id.ibtn_go_back);
        btnCheckAll = findViewById(R.id.ibtn_check_all);
        btnDone = findViewById(R.id.ibtn_done);
        goToTitleScreen = findViewById(R.id.ibtn_go_to_title_screen);

        folderPath.setSelected(true);                                                               //запускает анимацию прокрутки текста слева-направо
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        fileList.setLayoutManager(layoutManager);
    }

    private void initArgs() {
        args = new ArrayList<>();
        args.add(folderName);
        args.add(folderPath);
        args.add(goBack);
        args.add(btnCheckAll);
        args.add(btnDone);
    }

    protected void initController() {
        controller = new FileListControl(this, fileList, args);
    }

            /** !
    // нужна проверка на инициализацию класса, чтобы папки не сбивались при переворачивании экрана */
    private void showHomeFolders() {
        controller.showHomeFolders();                                                               //загружает или создаёт, а после отображает домашние папки
    }


    /** КНОПКИ НАВИГАЦИИИ */


    public void goToTitleScreen(View v) {
        super.onBackPressed();
    }

    public void goBack(View view) {
        if (controller.getCount() != 0) {
            controller.returnToPrevFolder();
        }
        else super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        goBack(goBack);
    }


    /** ФУНКЦИОНАЛЬНЫЕ КНОПКИ */


    public void selectAll(View view) {
        controller.selectAll();
    }

    public void done(View view) {
        controller.fillPlayList();

        super.onBackPressed();
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */