package com.andrey.guryanov.harmonica;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrey.guryanov.harmonica.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileList extends AppCompatActivity {

//    public RecyclerView fileList;
//    private FrameLayout fragmentPlace;
//    private ListView folderList;
//    private static List<String> navigation = new ArrayList<>();
//    private File[] listFiles;
//    private String pathSdCard;
//    private String pathSdCardMusic;
//    private String pathInternal;
//    private String pathInternalMusic;
//    private TextView text;
//    private List<Item> files;
//    private static List<Item> firstPaths;

//    public static List<Item> paths;

    private static final int PERMISSION_STORAGE = 100;
    private RecyclerView fileList;
    private TextView folderName, folderPath, console;
    private ImageButton goBack, goToTitleScreen, btnCheckAll, btnDone;
    private List<Object> args;
    private String playListName;
//    private String name, path;
    private Context thisContext;//тест


    private FileListControl controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        requestPermissions();
        initViews();
        initArgs();
        initController();
        //getBundleArgs();

        thisContext = this.getApplicationContext();//тест

        //playListName = "Default";   //в будущем надо будет расчехлять Бандл, чтобы получать конкретный плейлист



        controller.startAdapter(controller.getHomeFolders()); //создание адаптера для RecyclerView (первое, с домашними папками)
//        initFragment();
//        initAdapter(firstPaths);


    }

//    public Context getActivityContext() {//тест, потом удалю
//        return thisContext;//.getApplicationContext();
//    }   //test

//    protected void getBundleArgs() {
//        Bundle arguments = getIntent().getExtras();
//
//        PlayList playList;
//        if (arguments != null) {
//            playList = (PlayList) arguments.getSerializable("PlayList");
//            controller.setPlayList(playList);
//        }
//    }

    protected void requestPermissions() {
        if (PermissionUtils.hasPermissions(this)) return;
        PermissionUtils.requestPermissions(this, PERMISSION_STORAGE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == PERMISSION_STORAGE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (PermissionUtils.hasPermissions(this)) {
//                    tvPermission.setText("Разрешение получено");
//                } else {
//                    tvPermission.setText("Разрешение не предоставлено");
//                }
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    protected void initViews () {
//        folderList = findViewById(R.id.lv_folder_list);
        fileList = findViewById(R.id.rv_file_list);
        folderName = findViewById(R.id.tv_folder_name);
        folderPath = findViewById(R.id.tv_folder_path);
        goBack = findViewById(R.id.ibtn_go_back);
        console = findViewById(R.id.tv_console);
        btnCheckAll = findViewById(R.id.ibtn_check_all);
        btnDone = findViewById(R.id.ibtn_done);
        folderPath.setSelected(true);   //запускает анимацию прокрутки текста слева-направо

        goToTitleScreen = findViewById(R.id.ibtn_go_to_title_screen);
        //инициализация RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        fileList.setLayoutManager(layoutManager);

    }

    public void initArgs() {
        args = new ArrayList<>();
        args.add(folderName);
        args.add(folderPath);
        args.add(console);
        args.add(goBack);
        args.add(btnCheckAll);
        args.add(btnDone);
    }

//    protected void initAdapter(List<Item> paths) {
////        FileListAdapter adapter = new FileListAdapter(this, paths);
////        fileList.setAdapter(adapter);
//
////        FileListControl control = new FileListControl(this);
////        control.refreshFileList(fileList ,paths);
//
////        ArrayAdapter<Item> adapter = new ArrayAdapter<>(this, R.layout.file_list_item, R.id.tv_file_name, paths);
////        folderList.setAdapter(adapter);
//    }

    protected void initController() {
        controller = new FileListControl(this, fileList, args);
    }

    //  кнопки навигации {
    public void goToTitleScreen(View v) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        super.onBackPressed();
    }

//    public RecyclerView getFileList() {
//        return fileList;
//    }

//    public List<TextView> getText() {
//        return textArgs;
//    }

//    public void setNames() {
//        folderPath.setText(path);
//        folderPath.setText(name);
//    }
//
//    public void setCurrentFolderName(String newName, String newPath) {
////        folderName = findViewById(R.id.tv_folder_name);
////        folderPath = findViewById(R.id.tv_folder_path);
////
////        folderName.setText(newName);
////        folderPath.setText(newPath);
////        folderPath.setText();
//        this.name = newName;
//        this.path = newPath;
//        setNames();
//    }

//    public TextView getFolderName() {
//        return folderName;
//    }

    public void goBack(View view) {
//        FileList.prevNavigation();
//        currentAdapter.startAdapter(FileList.getCurrentPath());
        if (controller.getCount() != 0) {
            controller.goToBack();
            //folderName = controller.getNewfolderAttr;
        }
        else super.onBackPressed();

    }
    //  }



//    public void scanSongs(View v) {
////        pathSdCard = Environment.getExternalStorageDirectory();
////        File[] tempFileArray = new Player().findMusic(path);
////        listFiles = Arrays.asList(tempFileArray);
//
////        ArrayAdapter<File> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listFiles);
////        songList.setAdapter(adapter);
//
//    }

//    public void initFragment() {;
//        Fragment fragment = new ItemListFragment(this);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_fragment_place, fragment);
//        fragmentTransaction.commit();
//    }

//    public void initRecyclerView() {
//        fileList = view.findViewById(R.id.rv_file_list);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parent);
//        fileList.setLayoutManager(layoutManager);
//        FileListAdapter adapter = new FileListAdapter(parent, view, fileList, FileList.getFirstPaths());
//        fileList.setAdapter(adapter);
//
//        currentAdapter = adapter;
//    }


    @Override
    public void onBackPressed() {
        goBack(goBack);
        //super.onBackPressed();
    }

    public void selectAll(View view) {
        controller.checkAll();
    }

    public void done(View view) {   //при выполнении этого метода мы точно знаем, что плейлист уже создан. Сами плейлисты должны создаваться в других активностях
        controller.fillPlayList();

        super.onBackPressed();
    }

}