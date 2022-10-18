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

    private void requestPermissionsToReadFS() {
        if (Permission.hasPermissions(this)) return;
        Permission.requestPermissions(this, PERMISSION_STORAGE);
    }


    /**
     * ИНИЦИАЛИЗАЦИЯ ПЕРЕМЕННЫХ
     **/


    private void initViews () {
        fileList = findViewById(R.id.rv_file_list);
        folderName = findViewById(R.id.tv_folder_name);
        folderPath = findViewById(R.id.tv_folder_path);
        goBack = findViewById(R.id.ibtn_go_back);
        btnCheckAll = findViewById(R.id.ibtn_check_all);
        btnDone = findViewById(R.id.ibtn_done);
        folderPath.setSelected(true);                                                               //запускает анимацию прокрутки текста слева-направо

        goToTitleScreen = findViewById(R.id.ibtn_go_to_title_screen);
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

    private void showHomeFolders() {
        controller.startAdapter(controller.getHomeFolders());                                       //создание адаптера для RecyclerView (первое, с домашними папками)
    }

    protected void initController() {
        controller = new FileListControl(this, fileList, args);
    }


    /**
     * КНОПКИ НАВИГАЦИИИ
     **/


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
        //super.onBackPressed();
    }


    /**
     * ФУНКЦИОНАЛЬНЫЕ КНОПКИ
     **/


    public void selectAll(View view) {
        controller.checkAll();
    }

    public void done(View view) {   //при выполнении этого метода мы точно знаем, что плейлист уже создан. Сами плейлисты должны создаваться в других активностях
        controller.fillPlayList();

        super.onBackPressed();
    }

}


    /**
     * СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ)
     **/


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
